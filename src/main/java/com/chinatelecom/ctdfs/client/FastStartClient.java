package com.chinatelecom.ctdfs.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinatelecom.udp.core.lang.exception.CommonException;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class FastStartClient {

	public final static String JobTokenAttrName="token";
	
	private final static Logger logger=LogManager.getLogger(FastStartClient.class);
	
	private static String executePath="qt-faststart";
	
	private String inputFile=null;
	
	private String outputFile=null;
	
	private String workingDir=null;
	
	public String getWorkingDir() {
		return workingDir;
	}

	public FastStartClient setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
		return this;
	}
	
	public static String getExecutePath() {
		return executePath;
	}

	public static void setExecutePath(String executePath) {
		FastStartClient.executePath = executePath;
	}

	public String getInputFile() {
		return inputFile;
	}

	public FastStartClient setInputFile(String inputFile) {
		this.inputFile = inputFile;
		return this;
	}

	
	public String getOutputFile() {
		return outputFile;
	}

	public FastStartClient setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	/**
	 * 运行命令并返回结果
	 * @param jobToken 任务标识 
	 * @return
	 * @throws CommonException
	 */
	public int run(String jobToken) throws CommonException{
		ProcessBuilder ffmpegProcBuilder=new ProcessBuilder();
		List<String> arguments=new ArrayList<String>();
		arguments.add(executePath);
		if(inputFile==null){
			throw new CommonException("input file can't null",null);
		}
		else{
			arguments.add(inputFile);
		}
		if(outputFile==null){
			throw new CommonException("output file can't null",null);
		}
		else{
			arguments.add(outputFile);
		}
		if(workingDir!=null){
			ffmpegProcBuilder.directory(new File(workingDir));
		}
		ffmpegProcBuilder.command(arguments);
		try {
			logger.info(String.format("run %s in working dir %s", TextHelper.join(arguments," "),workingDir==null?"":workingDir));
			Process process = ffmpegProcBuilder.inheritIO().start();
			FFMpegClient.read(process.getInputStream(),logger);
			FFMpegClient.read(process.getErrorStream(),logger);
			return process.waitFor();
		} catch (IOException e) {
			throw new CommonException(e.getMessage(),e);
		} catch (InterruptedException e) {
			throw new CommonException(e.getMessage(),e);
		}
	}
	
	
}
