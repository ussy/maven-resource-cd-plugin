package net.pshared.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.pshared.maven.ResourceAutoPublisher.ResourceFilter;
import net.pshared.maven.ResourceAutoPublisher.ResourceUpdateTask;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ResourceUpdateTaskTest {

    @After
    public void after() {
        new File("target/a.txt").delete();
        new File("target/b.txt").delete();
        new File("target/c.txt").delete();
        new File("src/test/resources/c/d").delete();
        new File("src/test/resources/c").delete();
        new File("target/c/d").delete();
        new File("target/c").delete();
    }

    @Test
    public void testRun() throws IOException {
        File src = new File("src/test/resources");
        File target = new File("target");

        File dest = new File("target/c.txt");
        Assert.assertFalse(dest.exists());

        ResourceFilter filter = new ResourceFilter();
        ResourceUpdateTask task = new ResourceUpdateTask(src, target, filter);
        task.run();

        File update = new File("src/test/resources/c.txt");
        update.createNewFile();
        task.run();

        Assert.assertTrue(dest.exists());

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(dest);
            output.write(1);
        } finally {
            IOUtils.close(output);
        }
        
        task.run();
        
        update.delete();

        task.run();
        Assert.assertFalse(dest.exists());
    }
    
    @Test
    public void testDirectoryFileRun() throws IOException {
        File src = new File("src/test/resources");
        File target = new File("target");

        File dest = new File("target/c/d/c.txt");
        Assert.assertFalse(dest.exists());

        ResourceFilter filter = new ResourceFilter();
        ResourceUpdateTask task = new ResourceUpdateTask(src, target, filter);
        task.run();

        File update = new File("src/test/resources/c/d/c.txt");
        update.getParentFile().mkdirs();
        update.createNewFile();
        task.run();

        Assert.assertTrue(dest.exists());

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(dest);
            output.write(1);
        } finally {
            IOUtils.close(output);
        }
        
        task.run();
        
        update.delete();

        task.run();
        Assert.assertFalse(dest.exists());
        Assert.assertTrue("exist directory", dest.getParentFile().exists());
    }
}
