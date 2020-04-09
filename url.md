## 通用

权限错误返回
```
{
    "retCode": "21",
    "retMsg": "没有访问权限"
}
```

用户id错误
```
{
    "retCode": "11",
    "retMsg": "获取用户信息失败"
}
```

文件id错误
```
{
    "retCode": "12",
    "retMsg": "获取文件ID失败"
}
```


## 个人网盘部分

1、上传文件

url:"/addPersonalFile"

**入参**：multipart/form-data
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
  "file":    //上传文件
  "fileName":    //文件名
}
```

**出参**：

正常：
```
{
  "content":"{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

无上传文件：
```
{
  "content":"{
     \"retCode\":\"17\",
     \"retMsg\":\"上传文件格式错误\"
  }"
}
```

**测试数据**:
```
{
  "empeeAcct":8556016,
  "parentId":"8556016",
  "file":...,
  "fileName":"2019-7-14.docx"
}
```

**异常**：
```
{
  "content":"{
    \"retCode\":\"0\",
    \"retMsg\":\"失败！nested exception is org.apache.ibatis.type.TypeException: Could not set parameters for mapping: ParameterMapping{property='ID', mode=IN, javaType=class java.lang.Object, jdbcType=null, numericScale=null, resultMapId='null', jdbcTypeName='null', expression='null'}. Cause: org.apache.ibatis.type.TypeException: Error setting null for parameter #1 with JdbcType OTHER . Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. Cause: java.sql.SQLException: 无效的列类型: 1111\"
  }"
}
```

2、显示文件列表

url:"/listPersonalFile"

入参：application/x-www-form-urlencoded
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
}
```

出参

正常：
```
{
  "content":"{
    \"fileInfo\":[
      {\"FILE_SIZE\":\"1625047\",\"FILE_PARENT\":\"8556010\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556010/2019-7-14.docx\",\"FILE_ID\":\"4ADC9FDF8B0646B292CC409C82637C53\",
      \"FILE_PATH\":\"~\",\"FILE_NAME\":\"2019-7-14.docx\",\"FILE_OWNER\":8556010,\"FILE_CREATION_DATE\":\"2020-03-31 22:39:30\",
      \"FILE_DFSNAME\":\"temp\",\"FILE_STATUS\":1,
      \"FILE_CLASS\":1,
      \"FILE_TYPE\":\"docx\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 22:39:30\",
      \"IS_FACORITE\":1},
      {\"FILE_SIZE\":\"188885\",\"FILE_PARENT\":\"8556010\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556010/饶诗语个人简介.pdf\",\"FILE_ID\":\"36880837E02D41618D4A6DC8D232566C\",
      \"FILE_PATH\":\"~\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"FILE_OWNER\":8556010,\"FILE_CREATION_DATE\":\"2020-03-31 19:10:28\",
      \"FILE_DFSNAME\":\"temp\",\"FILE_STATUS\":1,
      \"FILE_CLASS\":1,
      \"FILE_TYPE\":\"pdf\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 19:10:28\",
      \"IS_FACORITE\":1}],
      \"retCode\":\"1\",
      \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**：
```
{
  "id":8556016,
  "parentId":"8556016"
}
```

3、下载指定文件

url:"/downloadPersonalFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "path":string    //下载本地路径
}
```

**出参**

正常：
```
{
  "content":"{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"5ABFF5F0E0944835ADC3F1DAEC9AA394",
  "path":"C:\百度网盘"
}
```


4、查看指定文件具体信息

url:"/listPersonalFileInfo"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
}
```

**出参**

正常：
```
{
  "content":"{
    \"fileInfo\":
    {\"FILE_SIZE\":\"1625047\",\"FILE_PARENT\":\"8556016\",\"FILE_SYS\":\"/home/disk/public/files/personal/8556016/2019-7-14.docx\",\"FILE_ID\":\"4ADC9FDF8B0646B292CC409C82637C53\",
    \"FILE_PATH\":\"~\",\"FILE_NAME\":\"2019-7-14.docx\",\"FILE_OWNER\":8556016,\"FILE_CREATION_DATE\":\"2020-03-31 22:39:30\",
    \"FILE_DFSNAME\":\"temp\",
    \"FILE_STATUS\":1,
    \"FILE_CLASS\":1,
    \"FILE_TYPE\":\"docx\",\"FILE_MODIFICATION_DATE\":\"2020-03-31 22:39:30\",
    \"IS_FACORITE"\:1},
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"5ABFF5F0E0944835ADC3F1DAEC9AA394"
}
```

5、新建文件夹

url:"/addPersonalFolder"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "parentId":string   //父文件id
  "fileName":string   //新建文件夹名
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "parentId":"8556016",
  "fileName":"新建文件夹"
}
```

6、文件重命名

url:"/renamePersonalFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "fileName":string   //文件重命名
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"6D73EFB450BB4F3E9B2C50D3D330F007",
  "fileName":"rename.pdf"
}
```

7、删除文件至回收站

url:"/deletePersonalFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

**测试数据**
```
{
  "id":8556016,
  "fileId":"6D73EFB450BB4F3E9B2C50D3D330F007"
}
```

8、查看回收站

url:"/listRecycler"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
}
```

**出参**

正常：
```
{
  "content": "{
    \"fileInfo\":[
      {\"FILE_SIZE\":\"188885\",\"FILE_CREATION_DATE\":\"2020-04-02 00:27:48\",
      \"FILE_PARENT\":\"8556016\",\"FILE_ID\":\"6D73EFB450BB4F3E9B2C50D3D330F007\",
      \"FILE_TYPE\":\"pdf\",\"FILE_NAME\":\"rename.pdf\",\"FILE_MODIFICATION_DATE\":\"2020-04-02 01:55:39\"},
      {\"FILE_SIZE\":\"0\",\"FILE_CREATION_DATE\":\"2020-04-02 00:28:05\",
      \"FILE_PARENT\":\"8556016\",\"FILE_ID\":\"0CB40286BB2745108F5D1116EFD65D21\",
      \"FILE_NAME\":\"新文件夹\",\"FILE_MODIFICATION_DATE\":\"2020-04-02 02:06:29\"}],
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

**测试数据**
```
{
  "id":8556016
}
```

9、回收站复原

url:"/restoreRecycler"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string  //文件id
  "parentId":string  //父文件id
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

未完成的功能：
文件夹和文件单独删除，文件夹彻底删除，文件复原（根据路径生成新的文件夹）
文件夹回收站，原文件新建同名文件夹，还原后自动合并


10、回收站彻底删除

url:"/deleteRecycler"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string  //文件id
  "status":int  //文件状态
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

## 部门网盘部分

1、添加部门文件

url:"/addDepartmentFile"

**入参**：multipart/form-data
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
  "file":    //上传文件
  "fileName":    //文件名
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

无上传文件：
```
{
  "content":"{
     \"retCode\":\"17\",
     \"retMsg\":\"上传文件格式错误\"
  }"
}
```

2、显示部门文件列表

url:"/listDepartmentFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "parentId":string   //父文件id
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
若为收藏文件含有COLLECTION_DATE一项，为空则未收藏
```
{
 "content": 
    "{\"fileInfo\":
        [{\"GROUP_LEVEL\":1,\"FILE_SIZE\":\"1625047\",\"FILE_CREATION_DATE\":\"2020-04-04 23:34:40\",
        \"ORG_ID\":510067614,\"FILE_PARENT\":\"H1110002\",\"COLLECTION_DATE\":\"2020-04-10 01:51:26\",\"FILE_ID\":\"6D036C1831584B8E93185442FE1CCD41\",
        \"FILE_TYPE\":\"docx\",\"FILE_PATH\":\"~\\\\省企业信息化部\\\\自主开发团队\",\"FILE_NAME\":\"rename.docx\",\"FILE_MODIFICATION_DATE\":\"2020-04-04 23:34:40\",
        \"GROUP_ID\":\"H1110002\"},{\"GROUP_LEVEL\":1,
        \"FILE_SIZE\":\"0\",\"FILE_CREATION_DATE\":\"2020-04-05 23:40:14\",
        \"ORG_ID\":510067614,\"FILE_PARENT\":\"H1110002\",\"FILE_ID\":\"9E392527B0884C849C810264DD4BCF58\",
        \"FILE_PATH\":\"~\\\\省企业信息化部\\\\自主开发团队\",
        \"FILE_NAME\":\"新文件夹\",\"FILE_MODIFICATION_DATE\":\"2020-04-05 23:41:44\",
        \"GROUP_ID\":\"H1110002\"}],\"retCode\":\"1\",
        \"retMsg\":\"成功！\"}",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

3、新建部门文件夹

url:"/addDepartmentFolder"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "parentId":string   //父文件id
  "fileName":    //文件夹名
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

4、下载部门文件

url:"/downloadDepartmentFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "localPath":    //下载本地目标地址
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

5、显示文件详细信息

url:"/listOrgFileInfo"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"fileInfo\":
      {\"GROUP_LEVEL\":1,
      \"FILE_SIZE\":1625047,
      \"ORG_ID\":510067614,\"FILE_DFSNAME\":\"rename.docx\",\"FILE_PARENT\":\"H1110002\",\"FILE_SYS\":\"/home/disk/public/files/org/省企业信息化部/自主开发团队/rename.docx\",\"FILE_ID\":\"6D036C1831584B8E93185442FE1CCD41\",
      \"FILE_TYPE\":\"docx\",\"FILE_PATH\":\"~\\\\省企业信息化部\\\\自主开发团队\",
      \"FILE_NAME\":\"rename.docx\",\"FILE_MODIFICATION_DATE\":\"2020-04-04 23:34:40\",
      \"GROUP_ID\":\"H1110002\",
      \"IS_FACORITE"\:1},
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"}",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

6、部门文件重命名

url:"/renameOrgFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "fileName":    //重命名名称
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

7、添加需审核文件

url:"/addAuditOrgFile"

**入参**：multipart/form-data
```
{
  "empeeAcct":int   //员工id
  "parentId":string   //父文件id
  "file":    //上传文件
  "fileName":    //文件名
  "groupId":     //目标组织父文件ID
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

目标文件夹已有同名文件：
```
{
  "content":"{
     \"retCode\":\"20\",
     \"retMsg\":\"目标文件重名\"
  }"
}
```

无上传文件：
```
{
  "content":"{
     \"retCode\":\"17\",
     \"retMsg\":\"上传文件格式错误\"
  }"
}
```

8、显示被审核文件列表

url:"/listAuditedFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
}
```

**出参**

正常：
```
{
   "content": 
      "{\"auditInfo\":[{\"REVIEW_TIME\":\"2020-04-07 17:26:37\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"F7D5B8D908FE4C4FA0BE9C330A234484\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":0},{\"REVIEW_TIME\":\"2020-04-07 17:08:58\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"4A19600F23AE436BBFC0FDEF1A31B830\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":2}],
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"}",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

9、显示审核文件列表

url:"/listAuditFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
}
```

**出参**

正常：
```
{
   "content": 
      "{\"auditInfo\":[{\"REVIEW_TIME\":\"2020-04-07 17:26:37\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"F7D5B8D908FE4C4FA0BE9C330A234484\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":0},{\"REVIEW_TIME\":\"2020-04-07 17:08:58\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"4A19600F23AE436BBFC0FDEF1A31B830\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":2}],
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"}",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

10、审核文件

url:"/auditFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string    //文件id
  "state":int        //审核结果（1通过2拒绝）
  "auditId":string   //审核项id
  "groupId":string   //目标文件父组织id
}
```

**出参**

正常：
```
{
   "content": 
      "{\"auditInfo\":[{\"REVIEW_TIME\":\"2020-04-07 17:26:37\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"F7D5B8D908FE4C4FA0BE9C330A234484\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":0},{\"REVIEW_TIME\":\"2020-04-07 17:08:58\",\"PATH_NAME\":\"~\\\\省企业信息化部\\\\IT规划与架构团队\",\"FK_FILE\":\"4A19600F23AE436BBFC0FDEF1A31B830\",
      \"FILE_NAME\":\"饶诗语个人简介.pdf\",\"REVIEW_STATUS\":2}],
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"}",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

## 通用部分

1、收藏文件

url:"/addFavoriteFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "type":int    //文件种类1个人文件夹2部门文件
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

type为空：
```
{
  "content": "{
    \"retCode\":\"22\",
    \"retMsg\":\"没有提供文件种类\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

2、显示收藏文件

url:"/listFavoriteFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
}
```

**出参**

正常：
根据文件状态（若为回收站中文件显示不同）
```
{
    "content": "{
      \"favoriteList\":[
        {\"FILESIZE\":\"3\",\"COLLECTION_DATE\":\"2020-04-09 17:01:14\",
        \"FILE_STATUS\":3,
        \"FILE_CLASS\":1,
        \"FILE_TYPE\":1,\"FK_FILE\":\"C127A24AA570417BB460D8582CE4643B\",
        \"FILENAME\":\"test.txt\",\"FILE_MODIFICATION_DATE\":\"2020-04-05 23:19:59\"}],
      \"retCode\":\"1\",
      \"retMsg\":\"成功！\"}",
    "contentIsJson": true,
    "reqeustSerial": 0,
    "result": 0
}
```

3、取消收藏文件

url:"/cancelFavoriteFile"

**入参**：application/x-www-form-urlencoded
```
{
  "id":int   //员工id
  "fileId":string   //文件id
  "type":int    //文件种类1个人文件夹2部门文件
}
```

**出参**

正常：
```
{
  "content": "{
    \"retCode\":\"1\",
    \"retMsg\":\"成功！\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```

type为空：
```
{
  "content": "{
    \"retCode\":\"22\",
    \"retMsg\":\"没有提供文件种类\"
  }",
  "contentIsJson": true,
  "reqeustSerial": 0,
  "result": 0
}
```