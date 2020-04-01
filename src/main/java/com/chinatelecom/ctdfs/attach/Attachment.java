package com.chinatelecom.ctdfs.attach;

import java.io.Serializable;
import java.sql.Timestamp;

import com.chinatelecom.udp.core.dataaccess.ormapping.DataObject;
import com.chinatelecom.udp.core.dataaccess.ormapping.FieldMapping;
import com.chinatelecom.udp.core.dataaccess.ormapping.TableMapping;
import com.chinatelecom.udp.core.dataaccess.ormapping.expression.FieldExpression;

@TableMapping(tableName = "AttachmentFile")
public class Attachment extends DataObject implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String C_ID = "ID";
	public FieldExpression F_ID = new FieldExpression(this, "ID");
	public static final String J_ID = "";
	private String ID;

	@FieldMapping(fieldName = "ID", method = "get")
	public String getID() {
		return ID;
	}

	@FieldMapping(fieldName = "ID", method = "set")
	public Attachment setID(String ID) {
		this.ID = ID;
		setModified("ID");
		return this;
	}

	public static final String C_CREATETIME = "CreateTime";
	public FieldExpression F_CREATETIME = new FieldExpression(this,
			"CreateTime");
	public static final String J_CREATETIME = "";
	private Timestamp CreateTime;

	@FieldMapping(fieldName = "CreateTime", method = "get")
	public Timestamp getCreateTime() {
		return CreateTime;
	}

	@FieldMapping(fieldName = "CreateTime", method = "set")
	public Attachment setCreateTime(Timestamp CreateTime) {
		this.CreateTime = CreateTime;
		setModified("CreateTime");
		return this;
	}

	public static final String C_MODIFYTIME = "ModifyTime";
	public FieldExpression F_MODIFYTIME = new FieldExpression(this,
			"ModifyTime");
	public static final String J_MODIFYTIME = "";
	private Timestamp ModifyTime;

	@FieldMapping(fieldName = "ModifyTime", method = "get")
	public Timestamp getModifyTime() {
		return ModifyTime;
	}

	@FieldMapping(fieldName = "ModifyTime", method = "set")
	public Attachment setModifyTime(Timestamp ModifyTime) {
		this.ModifyTime = ModifyTime;
		setModified("ModifyTime");
		return this;
	}

	public static final String C_RECORDSTATE = "RecordState";
	public FieldExpression F_RECORDSTATE = new FieldExpression(this,
			"RecordState");
	public static final String J_RECORDSTATE = "";
	private Integer RecordState;

	@FieldMapping(fieldName = "RecordState", method = "get")
	public Integer getRecordState() {
		return RecordState;
	}

	@FieldMapping(fieldName = "RecordState", method = "set")
	public Attachment setRecordState(Integer RecordState) {
		this.RecordState = RecordState;
		setModified("RecordState");
		return this;
	}

	public static final String C_FILENAME = "FileName";
	public FieldExpression F_FILENAME = new FieldExpression(this, "FileName");
	public static final String J_FILENAME = "";
	private String FileName;

	@FieldMapping(fieldName = "FileName", method = "get")
	public String getFileName() {
		return FileName;
	}

	@FieldMapping(fieldName = "FileName", method = "set")
	public Attachment setFileName(String FileName) {
		this.FileName = FileName;
		setModified("FileName");
		return this;
	}

	public static final String C_FILESIZE = "FileSize";
	public FieldExpression F_FILESIZE = new FieldExpression(this, "FileSize");
	public static final String J_FILESIZE = "";
	private long FileSize;

	@FieldMapping(fieldName = "FileSize", method = "get")
	public long getFileSize() {
		return FileSize;
	}

	@FieldMapping(fieldName = "FileSize", method = "set")
	public Attachment setFileSize(long FileSize) {
		this.FileSize = FileSize;
		setModified("FileSize");
		return this;
	}

	public static final String C_OWNER = "Owner";
	public FieldExpression F_OWNER = new FieldExpression(this, "Owner");
	public static final String J_OWNER = "";
	private String Owner;

	@FieldMapping(fieldName = "Owner", method = "get")
	public String getOwner() {
		return Owner;
	}

	@FieldMapping(fieldName = "Owner", method = "set")
	public Attachment setOwner(String Owner) {
		this.Owner = Owner;
		setModified("Owner");
		return this;
	}

	public static final String C_RELATIONTYPE = "RelationType";
	public FieldExpression F_RELATIONTYPE = new FieldExpression(this,
			"RelationType");
	public static final String J_RELATIONTYPE = "";
	private String RelationType;

	@FieldMapping(fieldName = "RelationType", method = "get")
	public String getRelationType() {
		return RelationType;
	}

	@FieldMapping(fieldName = "RelationType", method = "set")
	public Attachment setRelationType(String RelationType) {
		this.RelationType = RelationType;
		setModified("RelationType");
		return this;
	}

	public static final String C_RELATIONID = "RelationID";
	public FieldExpression F_RELATIONID = new FieldExpression(this,
			"RelationID");
	public static final String J_RELATIONID = "";
	private String RelationID;

	@FieldMapping(fieldName = "RelationID", method = "get")
	public String getRelationID() {
		return RelationID;
	}

	@FieldMapping(fieldName = "RelationID", method = "set")
	public Attachment setRelationID(String RelationID) {
		this.RelationID = RelationID;
		setModified("RelationID");
		return this;
	}

	public static final String C_PATH = "Path";
	public FieldExpression F_PATH = new FieldExpression(this, "Path");
	public static final String J_PATH = "";
	private String Path;

	@FieldMapping(fieldName = "Path", method = "get")
	public String getPath() {
		return Path;
	}

	@FieldMapping(fieldName = "Path", method = "set")
	public Attachment setPath(String Path) {
		this.Path = Path;
		setModified("Path");
		return this;
	}

	public static final String C_STOREFILENAME = "StoreFileName";
	public FieldExpression F_STOREFILENAME = new FieldExpression(this,
			"StoreFileName");
	public static final String J_STOREFILENAME = "";
	private String StoreFileName;

	@FieldMapping(fieldName = "StoreFileName", method = "get")
	public String getStoreFileName() {
		return StoreFileName;
	}

	@FieldMapping(fieldName = "StoreFileName", method = "set")
	public Attachment setStoreFileName(String StoreFileName) {
		this.StoreFileName = StoreFileName;
		setModified("StoreFileName");
		return this;
	}
}
