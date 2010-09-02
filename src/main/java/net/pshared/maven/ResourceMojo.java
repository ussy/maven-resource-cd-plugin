package net.pshared.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @goal execute
 */
public class ResourceMojo extends AbstractMojo {

    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    protected File outputDir;

    /**
     * @parameter default-value="src/main/resources"
     */
    protected File resourceDir;

    /**
     * @parameter default-value=1000
     */
    protected int polling = 1000;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final ResourceAutoPublisher publisher = new ResourceAutoPublisher(resourceDir, outputDir, polling);
        Signal.handle(new Signal("INT"), new SignalHandler() {

            @Override
            public void handle(Signal s) {
                if ("INT".equals(s.getName())) {
                    synchronized (publisher) {
                        publisher.shutdown();
                        publisher.notify();
                    }
                }
            }
        });

        publisher.execute();

        try {
            synchronized (publisher) {
                publisher.wait();
            }
        } catch (InterruptedException e) {
        }
    }

}
