package net.pshared.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class IOUtilsTest {

    @After
    public void after() {
        new File("target/a.txt").delete();
        new File("target/b.txt").delete();
    }

    @Test
    public void testCopyNewFile() throws IOException {
        File src = new File("src/test/resources/a.txt");
        File dest = new File("target/a.txt");
        Assert.assertFalse(dest.exists());

        IOUtils.copy(src, dest);
        Assert.assertTrue(dest.exists());

        assertFile(src, dest);
    }

    @Test
    public void testCopyExistFile() throws IOException {
        File src1 = new File("src/test/resources/a.txt");
        File dest = new File("target/b.txt");
        Assert.assertFalse(dest.exists());

        IOUtils.copy(src1, dest);
        Assert.assertTrue(dest.exists());

        File src2 = new File("src/test/resources/b.txt");
        IOUtils.copy(src2, dest);

        assertFile(src2, dest);
    }

    private void assertFile(File src, File dest) throws IOException {
        FileInputStream srcIs = null;
        FileInputStream destIs = null;
        try {
            srcIs = new FileInputStream(src);
            destIs = new FileInputStream(dest);
            byte[] srcBuffer = new byte[(int) srcIs.getChannel().size()];
            byte[] destBuffer = new byte[(int) destIs.getChannel().size()];
            Assert.assertTrue(srcBuffer.length == destBuffer.length);

            for (int i = 0; i < srcBuffer.length; i++) {
                if (srcBuffer[i] != destBuffer[i]) {
                    Assert.fail();
                }
            }
            Assert.assertTrue("same values", true);
        } finally {
            IOUtils.close(srcIs);
            IOUtils.close(destIs);
        }
    }
}
