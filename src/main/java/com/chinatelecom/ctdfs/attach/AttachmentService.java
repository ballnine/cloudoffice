package com.chinatelecom.ctdfs.attach;

import static com.chinatelecom.udp.core.dataaccess.ormapping.DataObject.RECORDSTATE_NORMAL;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinatelecom.ctdfs.bean.WebFileInfo;
import com.chinatelecom.udp.core.dataaccess.ormapping.DAOFactory;



public class AttachmentService {
	private final static Logger logger=LogManager.getLogger(AttachmentService.class);
	private static AttachmentService attachmentService;
	public static AttachmentService getInstance() {
		if(attachmentService==null) {
			attachmentService=new AttachmentService();
		}
		return attachmentService;
	}
	public String addFile(WebFileInfo webFile,String userName){
		Attachment attachment=DAOFactory.getDAOObject(Attachment.class);
		attachment.setID(UUID.randomUUID().toString());
		attachment.setModified(Attachment.C_CREATETIME, "sysdate");
		attachment.setModified(Attachment.C_MODIFYTIME, "sysdate");
		attachment.setRecordState(RECORDSTATE_NORMAL);
		attachment.setFileName(webFile.getFileName());
		attachment.setOwner(userName);
		attachment.setFileSize(webFile.getSize());
		attachment.setPath(webFile.getPath());
		attachment.setStoreFileName(webFile.getDfsFileName());
		try {
			attachment.doInsert();
			return attachment.getID();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
