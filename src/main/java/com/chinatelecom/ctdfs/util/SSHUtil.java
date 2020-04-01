package com.chinatelecom.ctdfs.util;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;

public class SSHUtil {
	/**
	 *实现服务器连接
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param privateKeyFile
	 * @return
	 * @throws IOException
	 */
	public static Connection getSSHConnection(String host,int port,String username,String password,String privateKeyFile) throws IOException {
	    Connection connection=new Connection(host,port);
	    connection.connect();

	    File file=new File(privateKeyFile);
	    boolean b=connection.authenticateWithPublicKey(username,file,password);
	    if (b){
	        return connection;
	    }else {
	        System.out.println("登录连接失败，请检查用户名、密码、私钥文件");
	        return null;
	    }
	}
	
	/**
	 *实现上传本地文件到服务器上指定目录
	 * @param conn SSH连接信息
	 * @param fileName D:\a.txt
	 * @param localPath 服务器地址路径：/opt/scf2/log/loanorder
	 * @throws IOException
	 */
	public static void putFile(Connection conn, String fileName,String localPath) throws IOException{

	    SCPClient scpClient=new SCPClient(conn);
	    scpClient.put(fileName,localPath);

	}
	
	/**
	 *实现下载服务器上的文件到本地指定目录
	 * @param conn SSH连接信息
	 * @param fileName 服务器上的文件地址/opt/scf2/log/loanorder/all.log
	 * @param localPath 本地路径：D:\
	 * @throws IOException
	 */
	public static void getFile(Connection conn, String fileName,String localPath) throws IOException{

	    SCPClient scpClient=new SCPClient(conn);
	    //String logPath="cd /opt/scf2/log/loanorder";
	    //Session session = conn.openSession();
	    //session.execCommand(logPath);//执行shell命令
	    scpClient.get(fileName,localPath);

	}
	
	/**
	 *实现删除服务器上的文件
	 * @param conn SSH连接信息
	 * @param fileName 服务器上的文件地址/opt/scf2/log/loanorder/all.log
	 * @param localPath 本地路径：D:\
	 * @throws IOException
	 */
	public static void deleteFile(Connection conn, String path) throws IOException{
		SFTPv3Client sftpClient = new SFTPv3Client(conn);
		sftpClient.rm(path);
	}
	
	/**
	 *实现上传服务器上的文件夹
	 * @param conn SSH连接信息
	 * @param fileName 服务器上的文件夹地址/opt/scf2/log/loanorder/all.log
	 * @throws IOException
	 */
	public static void putDir(Connection conn, String fileName) throws IOException{
		SFTPv3Client sftpClient = new SFTPv3Client(conn);
		sftpClient.mkdir(fileName, 777);
	}
	
	/**
	 *实现服务器文件的重命名
	 * @param conn SSH连接信息
	 * @param oldPath 服务器上的原文件地址/opt/scf2/log/loanorder/all.log
	 * @param newPath 服务器上的新文件名
	 * @throws IOException
	 */
	public static void renameFile(Connection conn, String oldPath, String newPath) throws IOException{
		
		SFTPv3Client sftpClient = new SFTPv3Client(conn);
		sftpClient.mv(oldPath, newPath);
		
	}
}
