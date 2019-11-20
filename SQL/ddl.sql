create table T_ATTACHMENT
(
	id int auto_increment
		primary key,
	sequence varchar(20) not null,
	file_name varchar(40) not null,
	file_path varchar(200) not null,
	upload_date timestamp default CURRENT_TIMESTAMP not null,
	uploader int not null,
	status decimal(2) default '1' not null comment '1 正常 0 已被删除',
	deleter int null,
	delete_date timestamp default CURRENT_TIMESTAMP null,
	real_file_name varchar(80) null,
	file_size bigint null
)
comment '附件表'
;

create index SYSTEM_FILE_SYSTEM_STAFF_id_fk
	on T_ATTACHMENT (uploader)
;

create table T_INFORM
(
	id int auto_increment,
	title varchar(100) not null,
	top tinyint(1) default '0' not null comment '是否置顶',
	create_date timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	status decimal(2) default '1' null comment '0 撤销  1 正常或过期',
	content text not null,
	creator int not null,
	attchment_list varchar(200) null,
	canceler int null comment '撤销人ID',
	cancel_date datetime null comment '撤销时间',
	outdate_operator int null comment '过期操作人ID',
	outdate_date datetime null comment '过期时间',
	constraint T_INFORM_id_uindex
		unique (id)
)
comment '系统通知表'
;

alter table T_INFORM
	add primary key (id)
;

create table T_STAFF
(
	id int(20) auto_increment
		primary key,
	name char(30) null,
	account char(30) not null,
	password char(64) not null,
	status decimal(2) default '1' not null comment '1 正常 2 禁用 0 失效（离职）',
	create_date timestamp default CURRENT_TIMESTAMP not null,
	update_date timestamp default CURRENT_TIMESTAMP null,
	last_login timestamp default CURRENT_TIMESTAMP null,
	address varchar(100) null,
	phone varchar(20) null,
	phone2 varchar(20) null,
	email varchar(20) null,
	birth_date varchar(8) null,
	sex int(4) default '0' null comment '0 未知　1男　2女',
	icon int(10) null comment '头像文件ID，关联附件表',
	constraint STAFF_pk
		unique (account)
)
;

create table T_STAFF_ACTION_LOG
(
	id int auto_increment,
	staff_account varchar(40) null comment '用户账号',
	staff_id int null comment '用户的id',
	request_uri varchar(100) null,
	request_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	request_source_ip varchar(100) null,
	request_method varchar(10) null,
	request_body varchar(600) null,
	controller_method varchar(200) null,
	error_message varchar(100) null comment '异常信息',
	success tinyint(1) default '0' null comment '请求是否处理成功',
	handling_time int null comment '处理时间/ms',
	response_time timestamp default CURRENT_TIMESTAMP not null,
	response varchar(2000) null,
	des varchar(100) not null comment '描述信息',
	constraint T_STAFF_ACTION_LOG_id_uindex
		unique (id)
)
comment '用户操作日志'
;

create index T_STAFF_ACTION_LOG_request_time_index
	on T_STAFF_ACTION_LOG (request_time)
;

alter table T_STAFF_ACTION_LOG
	add primary key (id)
;

create table T_STAFF_ROLE
(
	staff_id int not null,
	role_id int not null,
	des varchar(200) null,
	primary key (staff_id, role_id)
)
;

create index STAFF_ROLE_ROLE_id_fk
	on T_STAFF_ROLE (role_id)
;

create table T_SYSTEM_PERMISSION_NODE
(
	id int auto_increment,
	name varchar(100) not null comment '名称',
	des varchar(200) null,
	status decimal(2) default '1' null comment '1有效 0无效',
	module varchar(100) not null comment '组件名称',
	constraint SYSTEM_PERMISSION_NODE_id_uindex
		unique (id)
)
comment '系统权限控制点'
;

alter table T_SYSTEM_PERMISSION_NODE
	add primary key (id)
;

create table T_SYSTEM_ROLE
(
	id int auto_increment,
	name varchar(20) not null,
	name_show varchar(20) not null,
	des varchar(200) null,
	status decimal(2) default '1' null comment '1 正常 2 禁用',
	constraint ROLE_id_uindex
		unique (id),
	constraint SYSTEM_ROLE_name_uindex
		unique (name)
)
comment '系统角色表'
;

alter table T_SYSTEM_ROLE
	add primary key (id)
;

create table T_SYSTEM_PERMISSION_ROLE
(
	role_id int not null,
	permission_id int not null,
	constraint SYSTEM_PERMISSION_ROLE_SYSTEM_PERMISSION_NODE_id_fk
		foreign key (permission_id) references T_SYSTEM_PERMISSION_NODE (id),
	constraint SYSTEM_PERMISSION_ROLE_SYSTEM_ROLE_id_fk
		foreign key (role_id) references T_SYSTEM_ROLE (id)
)
;

create table T_SYSTEM_ROUTE
(
	id int auto_increment,
	path varchar(20) null,
	parent int null,
	async tinyint(1) default '1' null comment '是否权限控制路由 1是 0不是',
	des varchar(200) null comment '描述信息
',
	constraint SYSTEM_ROUTER_id_uindex
		unique (id),
	constraint SYSTEM_ROUTE_path_uindex
		unique (path)
)
comment '系统路由'
;

alter table T_SYSTEM_ROUTE
	add primary key (id)
;

create table T_SYSTEM_ROUTE_ROLE
(
	route_id int not null,
	role_id int not null
)
;

create table T_SYSTEM_SEQUENCE
(
	table_name varchar(20) not null,
	column_name varchar(20) not null,
	sequence int default '0' null,
	primary key (table_name, column_name)
)
;

