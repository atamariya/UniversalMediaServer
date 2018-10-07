package net.pms.io;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.pms.PMS;
import net.pms.configuration.PmsConfiguration;
import net.pms.configuration.RendererConfiguration;
import net.pms.database.Tables;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.GlobalIdRepo;
import net.pms.dlna.RealFile;
import net.pms.dlna.RootFolder;
import net.pms.dlna.SevenZipEntry;
import net.pms.dlna.SevenZipFile;
import net.pms.dlna.ZippedEntry;
import net.pms.dlna.ZippedFile;
import net.pms.encoders.FFMpegVideo;
import net.pms.util.FileWatcher;
import net.pms.util.StringUtil;
import net.pms.util.TaskRunner;

public class IOTest {
	public static void main(String[] args) throws Exception {
		new IOTest().testMediaLibraryFolder();
	}
	
	@Test
	public void testMediaLibraryFolder() throws Exception {
		String objectID = "0";
		boolean browseDirectChildren = true;
		int startingIndex = 0;
		int requestCount = -1;
		RendererConfiguration mediaRenderer = RendererConfiguration.getDefaultConf();
		RootFolder rootFolder = PMS.get().getRootFolder(null);
        List<DLNAResource> files = rootFolder.getDLNAResources(
				objectID,
				browseDirectChildren,
				startingIndex,
				requestCount,
				mediaRenderer,
				null
			);
		for(DLNAResource f : files)
		    StringUtil.printXML(StringUtil.unEncodeXML(f.getDidlString(mediaRenderer)));
	}

	@BeforeClass
    public static void setup() {
        try {
            // Use custom profile directory
            System.setProperty("UMS_PROFILE", "target/test-classes/resources");
            
            PmsConfiguration conf = new PmsConfiguration();
            RendererConfiguration.loadRendererConfigurations(conf);
            PMS.get().setConfiguration(conf);
            PMS.get().setRegistry(PMS.createSystemUtils());
            PMS.get().setGlobalRepo(new GlobalIdRepo());
            PMS.get().refreshLibrary(false);
            Tables.checkTables();
            
//            String[] args = new String[] {"headless"};
//            PMS.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Test
	public void testMediaScan() throws Exception {
		String dir = "src/test/resources/media" ;
		String db = "target/test-classes/resources/database/medias.mv.db";
		
		File database = new File(db);
//		database.delete();
		
		Files.walkFileTree(new File(dir).toPath(), EnumSet.of(FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs) throws IOException {
				DLNAResource resource = new RealFile(dir.toFile());
				resource = ((RealFile)resource).manageFile(dir.toFile());
				if (resource != null) {
					resource.setDefaultRenderer(RendererConfiguration.getDefaultConf());
					TaskRunner.getInstance().submit(resource);
				}
				
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				System.out.println("Failed: " + file);
				return FileVisitResult.CONTINUE;
			}
		});
		
//		TaskRunner.getInstance().awaitTermination(15, TimeUnit.SECONDS);

		List<DLNAMediaInfo> files = PMS.get().getDatabase().query("select * from files", null);
		System.out.println(files.size());
		for(DLNAMediaInfo f : files)
			System.out.println(f);
		
		FileWatcher.Listener reloader = new FileWatcher.Listener() {
			@Override
			public void notify(String filename, String event, FileWatcher.Watch watch, boolean isDir) {
				File f = new File(filename);
				DLNAResource resource = new RealFile(f);
				resource = ((RealFile)resource).manageFile(f);
				if (resource != null) {
					resource.setDefaultRenderer(RendererConfiguration.getDefaultConf());
					TaskRunner.getInstance().submit(resource);
				}
				System.out.println(String.format("%s %s", filename, event));
			}
		};
		PMS.getFileWatcher().add(new FileWatcher.Watch(dir + "**", reloader));
		PMS.get().refreshLibrary(false);
		
	}
	
	public void testFileConversion() throws Exception {
		// Test file conversion
		List<String> cmd = new ArrayList<>();
		cmd.add("bin\\win32\\ffmpeg64.exe");
		cmd.add("-i");
		cmd.add("utbr.mp3");
		cmd.add("-vn");
		cmd.add("-f");
		cmd.add("mp3");
		cmd.add("pipe:");
		String[] cmdArray = cmd.toArray(new String[cmd.size()]);
		OutputParams params = new OutputParams(null);
		
		ProcessWrapperImpl pw = new ProcessWrapperImpl(cmdArray, params);
		pw.runInNewThread();
		InputStream file = pw.getInputStream(0);
		PushbackInputStream pis = new PushbackInputStream(file);
		
		FileOutputStream os = new FileOutputStream("a.mp3");
		int read = file.read();
//		while(read != -1) {
//			os.write(read);
//			read = file.read();
//		}
		
		read = pis.read();
		byte[] bytes = new byte[1024 * 1024];
		while (read != -1) {
			read = pis.read();
			pis.unread(read);
			read = pis.read(bytes, 0, bytes.length);
			System.out.println(read);
			os.write(bytes);

			read = pis.read();
			pis.unread(read);
			System.out.println(read);
		}
		file.close();
		os.close();
	}
	
//	@Test
	public void testBufferIO() throws Exception {
		OutputParams params = new OutputParams(null);
		byte[] bytes = new byte[] {3, 4}; 
		BufferedOutputFileImpl file = new BufferedOutputFileImpl(params);
		file.write(bytes);

		int i = -1;
		int j = 0;
		byte[] buf = new byte[2];
		while (i++ < 10 && j >= 0) {
			j = file.read(i == 0, i);
			System.out.println(i + " " + j);
			j = file.read(false, i, buf , 0, 1);
			System.out.println(i + " " + j + " " + buf[0]);
			if (i == 3)
				file.detachInputStream();
		}
		file.close();
	}

	@Test
	public void test7Zip() {
	    File file = new File("src/test/resources/media/Untitled.7z");
	    SevenZipFile zipFile = new SevenZipFile(file);
	    SevenZipEntry entry = (SevenZipEntry) zipFile.getChildren().get(0);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
	        entry.push(baos);
	        
	        byte[] comp = Files.readAllBytes(Paths.get("src/test/resources/media/Untitled.jpg"));
	        Thread.sleep(1000);
	        assertArrayEquals(comp, baos.toByteArray());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	@Test
	public void testZip() {
        File file = new File("src/test/resources/media/Untitled.zip");
        ZippedFile zipFile = new ZippedFile(file);
        ZippedEntry entry = (ZippedEntry) zipFile.getChildren().get(0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            entry.push(baos);
            
            byte[] comp = Files.readAllBytes(Paths.get("src/test/resources/media/Untitled.jpg"));
            Thread.sleep(1000);
            assertArrayEquals(comp, baos.toByteArray());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	@Test
    public void testFFMpegVideo() {
        RendererConfiguration mediaRenderer = RendererConfiguration.getDefaultConf();
        RootFolder rootFolder = PMS.get().getRootFolder(null);
        DLNAResource dlna = rootFolder.getDLNAResource("176", mediaRenderer);

        DLNAMediaInfo media = new DLNAMediaInfo();
        media.setMimeType(dlna.getMedia().getMimeType());

        OutputParams params = new OutputParams(PMS.getConfiguration());
        params.mediaRenderer = PMS.getConfiguration();
        params.externalSubs = true;
        params.transcode = false;
        params.scale = true;

        FFMpegVideo player = new FFMpegVideo();
        try {
            player.launchTranscode(dlna, media, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@AfterClass
	public static void tearDown() {
        PMS.get().shutdown();
	}
}
