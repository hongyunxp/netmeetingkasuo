/*==============================================================*/
/* Table: E_USERMEETING   会议表                                                                                        */
/*==============================================================*/
create table E_MEETING
(
   MEETINGID            varchar(200) not null,
   VERIFYCODE           varchar(200) not null,
   SUBJECT              varchar(200) not null,
   AGENDA               varchar(200) not null,
   BEGINTIME            varchar(200) not null,
   DURATION             varchar(100),
   STATE                int default 0,
   CREATETIME           VARCHAR(100),
   UPDATETIME           VARCHAR(100),
   primary key (MEETINGID)
);

/*==============================================================*/
/* Table: E_USERMEETING   用户表                                                                                        */
/*==============================================================*/
create table E_USER
(
   USERID               VARCHAR(200) not null,
   PASSWORD             VARCHAR(200) not null,
   SESSIONID            VARCHAR(100) not null,
   USERNAME             VARCHAR(200) not null,
   USERROLE             INT not null default 1,
   USEREMAIL            VARCHAR(200),
   USERPIC              VARCHAR(200),
   USERCREATE           VARCHAR(200) not null,
   USERUPDATE           VARCHAR(200) not null,
   primary key (USERID)
);

/*==============================================================*/
/* Table: E_USERMEETING    会议用户关联表                                                                */
/*==============================================================*/
create table E_USERMEETING
(
   USERID               VARCHAR(200) not null,
   MEETINGID            VARCHAR(200) not null,
   USERMEETINGROLE      INT default 1,
   USERENTERTIME        VARCHAR(200) not null,
   MEETINGUSERSTATE     INT default 0,
   primary key (USERID, MEETINGID, USERMEETINGROLE)
);


/*==============================================================*/
/* Table: E_FILE      共享文档表                                                                                      */
/*==============================================================*/
create table E_FILE
(
   FILEID               VARCHAR(100) not null,
   FILENAME             VARCHAR(200) not null,
   FILEPATH             VARCHAR(200) not null,
   FILESIZE             VARCHAR(100) not null,
   FILECREATE           VARCHAR(100) not null,
   FILEPAGE             VARCHAR(100) not null,
   FILECOLLECTION       VARCHAR(8192) not null,
   FILEEXT              VARCHAR(100) not null,
   USERID               VARCHAR(200) not null,
   primary key (FILEID)
);


/*==============================================================*/
/* Table: E_MEETINGDOCUMENT      会议文档表                                                          */
/*==============================================================*/
create table E_MEETINGFILE
(
   MEETINGID            VARCHAR(200) not null,
   FILEID               VARCHAR(100) not null,
   primary key (MEETINGID, FILEID)
);


/*==============================================================*/
/* Table: E_VIDEO       视频表                                                                                           */
/*==============================================================*/
create table E_VIDEO
(
   VIDEOID              VARCHAR(100) not null,
   VIDEONAME            VARCHAR(200) not null,
   VIDEOPATH            VARCHAR(200) not null,
   VIDEOSIZE            VARCHAR(100) not null,
   VIDEOCREATE          VARCHAR(200) not null,
   VIDEOEXT             VARCHAR(100) not null,
   USERID               VARCHAR(200) not null,
   primary key (VIDEOID)
);

/*==============================================================*/
/* Table: E_MEETINGVIDEO    会议视频关联表                                                             */
/*==============================================================*/
create table E_MEETINGVIDEO
(
   MEETINGID            VARCHAR(200) not null,
   VIDEOID              VARCHAR(100) not null,
   primary key (MEETINGID, VIDEOID)
);


/*==============================================================*/
/* Table: E_CONFIG    会议配置表                                                                                      */
/*==============================================================*/
create table E_CONFIG
(
   USERID               VARCHAR(200) not null,
   CONFIGNAME           VARCHAR(200) not null,
   CONFIGVALUE          VARCHAR(500) not null,
   CONFIGTIME           VARCHAR(100) not null,
   primary key (USERID, CONFIGNAME)
);


/*==============================================================*/
/* View: V_USERMEETING_VIEW    用户会议视图                                                          */
/*==============================================================*/
create view V_USERMEETING_VIEW
as 
select
   U.USERID,
   U.PASSWORD,
   U.SESSIONID,
   U.USERNAME,
   U.USERROLE,
   U.USEREMAIL,
   U.USERPIC,
   U.USERCREATE,
   U.USERUPDATE,
   M.MEETINGID,
   M.VERIFYCODE,
   M.SUBJECT,
   M.AGENDA,
   M.BEGINTIME,
   M.DURATION,
   M.STATE,
   M.CREATETIME,
   M.UPDATETIME,
   UM.USERMEETINGROLE,
   UM.USERENTERTIME,
   UM.MEETINGUSERSTATE
from
   E_USER U,
   E_MEETING M,
   E_USERMEETING UM
where
   U.USERID = UM.USERID
   AND M.MEETINGID = UM.MEETINGID


/*==============================================================*/
/* View: V_DOCUMENT_USER    用户文档视图                                                          */
/*==============================================================*/
create view V_DOCUMENT_USER
as
select
   U.USERID,
   U.PASSWORD,
   U.SESSIONID,
   U.USERNAME,
   U.USERROLE,
   U.USEREMAIL,
   U.USERPIC,
   U.USERCREATE,
   U.USERUPDATE,
   F.FILEID,
   F.FILENAME,
   F.FILEPATH,
   F.FILESIZE,
   F.FILECREATE,
   F.FILEPAGE,
   F.FILECOLLECTION,
   F.FILEEXT
from
   E_USER U,
   E_FILE F
where
   U.USERID = F.USERID


/*==============================================================*/
/* View: V_DOCUMENT_MEETING    会议文档视图                                                          */
/*==============================================================*/
create view V_DOCUMENT_MEETING
as
select
   M.MEETINGID,
   M.VERIFYCODE,
   M.SUBJECT,
   M.AGENDA,
   M.BEGINTIME,
   M.DURATION,
   M.STATE,
   M.CREATETIME,
   M.UPDATETIME,
   F.FILEID,
   F.FILENAME,
   F.FILEPATH,
   F.FILESIZE,
   F.FILECREATE,
   F.FILEPAGE,
   F.FILECOLLECTION,
   F.FILEEXT
from
   E_MEETING M,
   E_FILE F,
   E_MEETINGFILE MD
where
   M.MEETINGID = MD.MEETINGID
   and 
   F.FILEID = MD.FILEID


/*==============================================================*/
/* View: V_DOCUMENT_USER_MEETING    用户会议文档视图                                  */
/*==============================================================*/
create view V_DOCUMENT_USER_MEETING
as
select
   U.USERID,
   U.PASSWORD,
   U.SESSIONID,
   U.USERNAME,
   U.USERROLE,
   U.USEREMAIL,
   U.USERPIC,
   U.USERCREATE,
   U.USERUPDATE,
   M.MEETINGID,
   M.VERIFYCODE,
   M.SUBJECT,
   M.AGENDA,
   M.BEGINTIME,
   M.DURATION,
   M.STATE,
   M.CREATETIME,
   M.UPDATETIME,
   F.FILEID,
   F.FILENAME,
   F.FILEPATH,
   F.FILESIZE,
   F.FILECREATE,
   F.FILEPAGE,
   F.FILECOLLECTION,
   F.FILEEXT
from
   E_MEETING M,
   E_FILE F,
   E_MEETINGFILE MD,
   E_USER U
where
   M.MEETING = MD.MEETINGID
   and 
   F.FILEID = MD.FILEID
   and
   F.USERID = U.USERID
   
   
/*==============================================================*/
/* View: V_USER_VIDEO    用户视频视图                                                                          */
/*==============================================================*/
create view V_USER_VIDEO
AS
select 
   U.USERID,
   U.PASSWORD,
   U.SESSIONID,
   U.USERNAME,
   U.USERROLE,
   U.USEREMAIL,
   U.USERPIC,
   U.USERCREATE,
   U.USERUPDATE,
   V.VIDEOID,
   V.VIDEONAME,
   V.VIDEOPATH,
   V.VIDEOSIZE,
   V.VIDEOCREATE,
   V.VIDEOEXT
FROM
   E_USER AS U,
   E_VIDEO AS V
WHERE 
   V.USERID = U.USERID
   

/*==============================================================*/
/* View: V_MEETING_VIDEO    会议视频视图                                                                          */
/*==============================================================*/
create view V_MEETING_VIDEO
AS
select
   M.MEETINGID,
   M.VERIFYCODE,
   M.SUBJECT,
   M.AGENDA,
   M.BEGINTIME,
   M.DURATION,
   M.STATE,
   M.CREATETIME,
   M.UPDATETIME,
   V.VIDEOID,
   V.VIDEONAME,
   V.VIDEOPATH,
   V.VIDEOSIZE,
   V.VIDEOCREATE,
   V.VIDEOEXT,
   V.USERID
from
   E_MEETING M,
   E_VIDEO V,
   E_MEETINGVIDEO MV
where
   M.MEETINGID = MV.MEETINGID
   and 
   V.VIDEOID = MV.VIDEOID
   
   
/*==============================================================*/
/* 初始化创建                                                                                                                                           */
/*==============================================================*/

INSERT INTO E_CONFIG VALUES('0','EXPIRED','5','2011-01-01 11:11:11');
INSERT INTO E_CONFIG VALUES('0','PORT','5520','2011-01-01 11:11:11');
INSERT INTO E_CONFIG VALUES('0','BASEPATH','D:/EapStudio1.2/workspace/openeap/netmeeting','2011-01-01 11:11:11');
INSERT INTO E_CONFIG VALUES('0','CLEANINTERVAL','30','2011-01-20 08:56:34')
INSERT INTO E_CONFIG VALUES('0','SECURITY','3194','2011-03-10 09:32:05')
INSERT INTO E_CONFIG VALUES('0','ALLOWHANDUP','1','2011-03-10 09:32:05')
INSERT INTO E_CONFIG VALUES('0','ALLOWDESKTOPCONTROL','1','2011-03-10 09:32:05')
INSERT INTO E_CONFIG VALUES('0','ALLOWWHITEBOARD','0','2011-03-10 09:32:05')

INSERT INTO E_USER VALUES('admin','suntek','0','管理员','0','admin@gmail.com','','2011-01-19 11:11:11','2011-01-19 11:11:11')
