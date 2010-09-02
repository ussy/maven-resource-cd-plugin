package net.pshared.maven;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ResourceAutoPublisher {

    private File resourceDir;

    private File outputDir;

    private long delay;

    private ResourceFilter filter;

    private ScheduledExecutorService executor;

    public ResourceAutoPublisher(File resourceDir, File outputDir, long delay) {
        this(resourceDir, outputDir, delay, null);
    }

    public ResourceAutoPublisher(File resourceDir, File outputDir, long delay, String filter) {
        this.resourceDir = resourceDir;
        this.outputDir = outputDir;
        this.delay = delay;
        this.filter = new ResourceFilter(filter);
    }

    public void execute() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new ResourceUpdateTask(resourceDir, outputDir, filter), 0, delay, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public boolean isShutdown() {
        if (executor == null) {
            return true;
        }

        return executor.isShutdown();
    }

    static class ResourceUpdateTask implements Runnable {

        private Set<File> all = new TreeSet<File>();

        private Map<String, Long> cache = new HashMap<String, Long>();

        private ResourceFilter filter;

        private File resourceDir;

        private File outputDir;

        public ResourceUpdateTask(File resourceDir, File outputDir, ResourceFilter filter) {
            this.resourceDir = resourceDir;
            this.outputDir = outputDir;
            this.filter = filter;
        }

        private List<File> search(File dir) {
            List<File> list = new ArrayList<File>();
            File[] files = dir.listFiles(filter);
            if (files == null) {
                return list;
            }
            
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(search(file));
                } else {
                    list.add(file);
                }
            }

            return list;
        }

        private boolean isUpdateFile(File file) {
            String path = file.getAbsolutePath();
            Long lastModified = cache.get(path);
            if (lastModified == null) {
                return true;
            }

            return file.lastModified() != lastModified;
        }

        @Override
        public void run() {
            try {
                List<File> list = search(resourceDir);
                for (File file : list) {
                    if (isUpdateFile(file)) {
                        cache.put(file.getAbsolutePath(), file.lastModified());
                        String relativePath = file.getAbsolutePath().substring(resourceDir.getAbsolutePath().length());

                        File newFile = new File(outputDir, relativePath);
                        newFile.getParentFile().mkdirs();
                        IOUtils.copy(file, newFile);

                        System.out.println(String.format("copy %s", newFile.getPath()));
                    }

                    all.remove(file);
                }

                for (File file : all) {
                    cache.remove(file.getAbsolutePath());

                    String relativePath = file.getAbsolutePath().substring(resourceDir.getAbsolutePath().length());
                    File newFile = new File(outputDir, relativePath);
                    newFile.getParentFile().mkdirs();
                    newFile.delete();
                    all.remove(file);

                    System.out.println(String.format("delete %s", newFile.getPath()));
                }
                all.addAll(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class ResourceFilter implements FileFilter {

        private Pattern pattern;

        public ResourceFilter() {
            this(null);
        }

        public ResourceFilter(String filter) {
            pattern = (filter == null) ? null : createPattern(filter);
        }

        @Override
        public boolean accept(File pathname) {
            if (pathname.isHidden()) {
                return false;
            }

            if (pattern == null) {
                return true;
            }

            return pattern.matcher(pathname.getName()).matches();
        }

        public static Pattern createPattern(String glob) {
            if (glob == null) {
                throw new NullPointerException("arguments not null");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("^");
            for (int i = 0; i < glob.length(); ++i) {
                char ch = glob.charAt(i);
                switch (ch) {
                case '*':
                    sb.append(".*");
                    break;
                case '?':
                    sb.append(".");
                    break;
                case '.':
                    sb.append("\\.");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(ch);
                }
            }

            sb.append("$");
            return Pattern.compile(sb.toString());
        }

    }
}
