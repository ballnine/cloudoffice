package com.chinatelecom.ctdfs.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinatelecom.udp.core.lang.exception.CommonException;
import com.chinatelecom.udp.core.lang.text.TextHelper;
@Component
public class FFMpegClient {
public final static String JobTokenAttrName="token";
	
	private final static Logger logger=LogManager.getLogger(FFMpegClient.class);
	
	private static String executePath="ffmpeg";
	
	private String inputFile=null;
	
	private String videoEncoder="libx264";
	
	private String audioEncoder="aac";
	
	private int hls_splitTime=10;
	
	private int hls_listSize=0;
	
	private String hls_fileName="file%03d.ts";
	
	private int strict=-2;
	
	private String format="hls";
	
	private String outputFile="output.m3u8";
	
	private String workingDir=null;
	
	private String progressUrl=null;
	
	private String baseUrl=null;
	

	public String getProgressUrl() {
		return progressUrl;
	}

	public FFMpegClient setProgressUrl(String progressUrl) {
		this.progressUrl = progressUrl;
		return this;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public FFMpegClient setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
		return this;
	}

	public static String getExecutePath() {
		return executePath;
	}

	public static void setExecutePath(String executePath) {
		FFMpegClient.executePath = executePath;
	}

	public String getInputFile() {
		return inputFile;
	}

	public FFMpegClient setInputFile(String inputFile) {
		this.inputFile = inputFile;
		return this;
	}

	public String getVideoEncoder() {
		return videoEncoder;
	}

	public FFMpegClient setVideoEncoder(String videoEncoder) {
		this.videoEncoder = videoEncoder;
		return this;
	}

	public String getAudioEncoder() {
		return audioEncoder;
	}

	public FFMpegClient setAudioEncoder(String audioEncoder) {
		this.audioEncoder = audioEncoder;
		return this;
	}

	public int getHls_splitTime() {
		return hls_splitTime;
	}

	public FFMpegClient setHls_splitTime(int hls_splitTime) {
		this.hls_splitTime = hls_splitTime;
		return this;
	}

	public int getHls_listSize() {
		return hls_listSize;
	}

	public FFMpegClient setHls_listSize(int hls_listSize) {
		this.hls_listSize = hls_listSize;
		return this;
	}

	public String getHls_fileName() {
		return hls_fileName;
	}

	public FFMpegClient setHls_fileName(String hls_fileName) {
		this.hls_fileName = hls_fileName;
		return this;
	}

	public int getStrict() {
		return strict;
	}

	public FFMpegClient setStrict(int strict) {
		this.strict = strict;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public FFMpegClient setFormat(String format) {
		this.format = format;
		return this;
	}
	
	public String getOutputFile() {
		return outputFile;
	}

	public FFMpegClient setOutputFile(String outputFile) {
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
			arguments.add("-i");
			arguments.add(inputFile);
		}
		if(videoEncoder!=null){
			arguments.add("-c:v");
			arguments.add(videoEncoder);
		}
		if(audioEncoder!=null){
			arguments.add("-c:a");
			arguments.add(audioEncoder);
		}
		arguments.add("-hls_time");
		arguments.add(String.valueOf(hls_splitTime));
		arguments.add("-hls_list_size");
		arguments.add(String.valueOf(hls_listSize));
		if(hls_fileName!=null){
			arguments.add("-hls_segment_filename");
			arguments.add(hls_fileName);
		}
		if(baseUrl!=null){
			arguments.add("-hls_base_url");
			arguments.add(baseUrl);
		}
		arguments.add("-strict");
		arguments.add(String.valueOf(strict));
		arguments.add("-f"); 
		arguments.add(format);
		if(progressUrl!=null){
			arguments.add("-progress");
			if(progressUrl.contains("?")){
				arguments.add(String.format("%s&%s=%s", progressUrl,JobTokenAttrName,jobToken));
			}
			else{
				arguments.add(String.format("%s?%s=%s", progressUrl,JobTokenAttrName,jobToken));
			}
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
			read(process.getInputStream(),logger);
			read(process.getErrorStream(),logger);
			return process.waitFor();
		} catch (IOException e) {
			throw new CommonException(e.getMessage(),e);
		} catch (InterruptedException e) {
			throw new CommonException(e.getMessage(),e);
		}
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public FFMpegClient setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	// 读取输入流
	public static void read(InputStream inputStream,Logger outputLogger) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				outputLogger.info(line);
			}
		} catch (IOException e) {
			outputLogger.error(e.getMessage(), e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
	}
}
