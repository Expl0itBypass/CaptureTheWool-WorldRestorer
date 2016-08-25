package com.gmail.lynx7478.ctw.worldrestorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldRestorer extends JavaPlugin {
	
	@Override
    public void onEnable()
    {
        File worldDirectoy = new File(new File(getDataFolder().getParent(),"CaptureTheWool"),"Worlds");
        File backupDirectory = new File(new File(getDataFolder().getParent(),"CaptureTheWool"),"WorldBackups");
        if(!backupDirectory.exists())
            backupDirectory.mkdir();

        File[] worldFiles = worldDirectoy.listFiles();
        //File[] backupFiles = backupDirectory.listFiles();

        for(int x = 0; x < worldFiles.length; x++)
        {
            File file = worldFiles[x];

            //If its not a directory we ignore it
            if(!file.isDirectory())
                continue;

            //If the world is in the backup directory, we restore it
            File bFile = searchFolder(backupDirectory,file.getName());
            if(bFile != null)
            {
                file.delete();
                try
                {
                    copy(bFile,file);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                Bukkit.getLogger().info("[CaptureTheWool] Copying over world \""+bFile.getName()+"\" from the backup directory.");
                continue;
            }


            //If there is no file with this name in the backup directory,
            //then we copy the folder to the backup directory
            try
            {
                copy(file,new File(backupDirectory,file.getName()));
                Bukkit.getLogger().info("[CaptureTheWool] Backing up world \"" + file.getName() + "\" in the backups folder.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private File searchFolder(File folder, String nameToFind)
    {
        for(File file : folder.listFiles())
            if(file.getName().equals(nameToFind))
                return file;
        return null;
    }

    void copy(File source, File destination) throws IOException
    {
        if (source.isDirectory())
        {

            //if directory not exists, create it
            if (!destination.exists())
            {
                destination.mkdir();
            }

            //list all the directory contents
            String files[] = source.list();

            for (String file : files)
            {
                //construct the src and dest file structure
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                //recursive copy
                copy(srcFile, destFile);
            }

        }
        else
        {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

}
