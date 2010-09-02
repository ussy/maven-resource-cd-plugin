package net.pshared.maven;

import java.io.File;

import net.pshared.maven.ResourceAutoPublisher.ResourceFilter;

import org.junit.Assert;
import org.junit.Test;

public class ResourceFilterTest {

    @Test
    public void testNullFilter() {
        ResourceFilter filter = new ResourceFilter();

        Assert.assertFalse("hidden file", filter.accept(new File("src/test/resources/.hidden.txt")));
        Assert.assertTrue(filter.accept(new File("src/test/resources/a.txt")));
        Assert.assertTrue(filter.accept(new File("src/test/resources/not_exists.txt")));
    }
    
    @Test
    public void testTextFilter() {
        ResourceFilter filter = new ResourceFilter("*.txt");

        Assert.assertFalse("hidden file", filter.accept(new File("src/test/resources/.hidden.txt")));
        Assert.assertTrue(filter.accept(new File("src/test/resources/a.txt")));
        Assert.assertFalse(filter.accept(new File("src/test/resources/a.properties")));
        Assert.assertTrue(filter.accept(new File("src/test/resources/not_exists.txt")));
    }
}
