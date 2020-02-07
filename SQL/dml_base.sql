# 新建root角色
INSERT INTO T_SYSTEM_ROLE (name, name_show, des, status) VALUES ('root', 'ROOT管理员', null, 1);

# 新建root用户
INSERT INTO T_STAFF (name, account, password, status) VALUES ('ROOT用户', 'root', '96E79218965EB72C92A549DD5A330112', 1);

# 绑定账户与角色
INSERT INTO T_STAFF_ROLE(staff_id, role_id, des) SELECT t1.id, t2.id, '系统初始化' from T_STAFF t1 ,T_SYSTEM_ROLE t2 where t1.account='root' and t2.name='root'

# 新建root相关的前端路由信息
INSERT INTO T_SYSTEM_ROUTE (path, parent, async, des) VALUES ('/root', null, 1, 'Root功能');
INSERT INTO T_SYSTEM_ROUTE (path, parent, async, des) VALUES ('/root/permission', 1, 1, '权限细节');
INSERT INTO T_SYSTEM_ROUTE (path, parent, async, des) VALUES ('/root/menu', 1, 1, '路由权限');
INSERT INTO T_SYSTEM_ROUTE (path, parent, async, des) VALUES ('/root/role', 1, 1, '角色管理');

# 绑定路由与角色
INSERT INTO T_SYSTEM_ROUTE_ROLE (route_id, role_id) SELECT t1.id, t2.id FROM T_SYSTEM_ROUTE t1, T_SYSTEM_ROLE t2 WHERE t1.path LIKE '/root%' and t2.name = 'root'

# 添加前端组件权限点
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_add', '添加用户按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_forbidden', '禁用用户按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_role_setting', '角色配置按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_password_reset', '重置密码按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_lift', '用户解禁按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('button_dimission', '用户离职按钮', 1, 'UserManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('tab_add', '新增公告tab栏', 1, 'InformManagement');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('top_and_cancel', '置顶公告以及取消置顶', 1, 'InformTable');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('cancel', '撤销公告', 1, 'InformTable');
INSERT INTO T_SYSTEM_PERMISSION_NODE (name, des, status, module) VALUES ('outdate', '设置公告过期', 1, 'InformTable');