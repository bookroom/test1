package com.ziptest.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Hzipclient extends Thread {

	
	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		try {
			Socket s=new Socket("192.168.0.1",10100);
			InputStream ins=s.getInputStream();
			ZipInputStream zis=new ZipInputStream(ins);
			
			String rootDir=null;
			//在此次连接中，是否是第一次读到ZipEntry。读到的第一个Entry，就是正在被传输的文件夹。
			boolean isFirst=true;
			String savePath="D:"+File.separator+"gitspace"+File.separator;
			//以上为保存接收到的文件夹的位置。
			//如，服务端传输的文件夹是D:\zipfolder，则该文件夹在客户端将保存在D:\gitspace\。
			//为了良好的移植性，这里用File.separator，
			//因为分隔符在不同的操作系统上，可能不一样。
			
			ZipEntry ze=null;
			ZipOutputStream zos=null;
			FileOutputStream fos=null;
			OutputStream os=null;
			byte[] b=new byte[1024];
			int length=-1;
			
			while( (ze=zis.getNextEntry())!=null  )
			{
				String name=ze.getName();
				File file=null;
				if(ze.isDirectory())
				{
					if(isFirst)
					{
						isFirst=false;
						
						//这里可能移植性不好，我还没研究透，暂用此法。
						String[] temp=name.split("\\\\");
						
						//文件夹的名字。参看服务端代码可知，这个名字是“/”结尾的。
						String selfName=temp[temp.length-1];
						
						//去掉“/”，这里才是真正的文件夹的名字。
						//后面接收到的文件将要以rootDir为参照物，确定保存位置。
						rootDir=selfName.substring(0, selfName.length()-1);
						
						//到了这里，这个文件夹的路径其实就是D:\gitspace\zipfolder了。
						file=new File(savePath+selfName);
					}
					
					int index=name.indexOf(rootDir);
					
					//如，该文件在服务端的位置是D:\zipfolder\a.txt,则tempDir=zipfolder\a.txt
					String tempDir=name.substring(index,name.length());
					
					//两者拼合，该文件在客户端的保存路径也就是D:\gitspace\zipfolder\a.txt了。
					//相对于ziplolder，该文件的位置未发生变化。
					file=new File(savePath+tempDir);
					
					if(!file.exists())
					{
						file.mkdirs();
					}
				}
				else
				{
					int index=name.indexOf(rootDir);
					String tempFileDir=name.substring(index,name.length());
					file=new File(savePath+tempFileDir);
					fos=new FileOutputStream(file);
					while( (length=zis.read(b))>-1  )
					{
						fos.write(b, 0, length);
					}
					
					//这句必须有，不然文件多了，打开的文件过多，将会发生异常
					fos.close();
				}
			}
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            继续修改
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
