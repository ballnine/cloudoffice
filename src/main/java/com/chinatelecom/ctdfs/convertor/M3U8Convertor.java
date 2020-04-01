package com.chinatelecom.ctdfs.convertor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chinatelecom.ctdfs.bean.WebFileInfo;
import com.chinatelecom.ctdfs.client.FFMpegClient;
import com.chinatelecom.ctdfs.handler.IFormFieldInfo;
import com.chinatelecom.udp.component.ctdfs.CTDFSFileSystem;
import com.chinatelecom.udp.core.lang.exception.CommonException;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class M3U8Convertor implements IConvertor {
	private final static Logger logger = LogManager.getLogger(M3U8Convertor.class);
	@Autowired
	private FFMpegClient client;

	@Override
	public List<IFormFieldInfo> convert(List<IFormFieldInfo> webFields) {
		// TODO Auto-generated method stub
		List<IFormFieldInfo> m3u8Fields = new ArrayList<IFormFieldInfo>();
		for (IFormFieldInfo webField : webFields) {
			if (webField.isFileField()) {
				WebFileInfo webFile = (WebFileInfo) webField;
				String fileName = TextHelper.getLeftPart(webFile.getDfsFileName(), ".", true);
				try {
					int result = client.setInputFile(webFile.getDfsFileName()).setOutputFile(fileName + ".m3u8")
							.setHls_fileName(fileName + "-%03d.ts").run(UUID.randomUUID().toString());
					if (result == 0) {
						File[] files = new File(webFile.getPath()).listFiles(new VideoFileFitler(fileName));
						for (File file : files) {
							try {
								CTDFSFileSystem.getInstance().uploadFile(file.getAbsolutePath(),
										webFile.getPath() + file.getName());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						m3u8Fields.add(new WebFileInfo(client.getOutputFile(), client.getOutputFile(),
								new File(webFile.getPath(), client.getOutputFile()).length(), webFile.getPath()));
					} else {
						logger.error("m3u8 file generate failed");
					}
				} catch (CommonException e) {
					logger.error(e.getMessage(), e);
				} finally {
					File[] files = new File(webFile.getPath()).listFiles(new VideoFileFitler(fileName));
					for (File file : files) {
						file.delete();
					}
				}
			}
		}
		return m3u8Fields;
	}
}
