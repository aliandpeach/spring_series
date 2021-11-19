package com.fs;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.jimfs.Feature.FILE_CHANNEL;
import static com.google.common.jimfs.Feature.LINKS;
import static com.google.common.jimfs.Feature.SYMBOLIC_LINKS;
import static com.google.common.jimfs.PathNormalization.CASE_FOLD_ASCII;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/04 18:22:14
 */
public class FileTest
{
    private static class Item
    {
        private List<String> items;

        public List<String> getItems()
        {
            return items;
        }

        public void setItems(List<String> items)
        {
            this.items = items;
        }
    }

    public static void main(String[] args) throws IOException
    {
        /*File file = new File("F:\\Download\\ideaIU-2021.1.exe");
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel();
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                *//*FileOutputStream fileOut = new FileOutputStream("F:\\Download\\ideaIU-2021.1-1.exe")*//*)
        {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int len;
            while ((len = fileChannel.read(buffer)) != -1)
            {
                buffer.flip();
                byteOut.write(buffer.array(), 0, len);
                buffer.clear();
            }
        }
        System.out.println("over!!!");*/


        String fileName = "1.";
        String fileSuffix = null == fileName || fileName.lastIndexOf(".") == -1 ? "" : "*" + fileName.substring(fileName.lastIndexOf("."));

        Item item = new Item();
        List<String> o = Optional.ofNullable(item.getItems()).orElse(new ArrayList<>());

        Configuration config = Configuration.builder(PathType.windows())
                .setRoots("F:\\")
                .setWorkingDirectory("F:\\work")
                .setNameCanonicalNormalization(CASE_FOLD_ASCII)
                .setPathEqualityUsesCanonicalForm(true) // matches real behavior of WindowsPath
                .setAttributeViews("basic")
                .setSupportedFeatures(LINKS, SYMBOLIC_LINKS, FILE_CHANNEL)
                .build();

        FileSystem fs = Jimfs.newFileSystem(config);
        Path foo = fs.getPath("F:\\Download");
        Files.createDirectory(foo);

        Path hello = foo.resolve("1.txt");
        Path path = Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
        System.out.println("over!!!2");
    }
}
