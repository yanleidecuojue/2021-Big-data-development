#### 一. MYSQL基础知识

##### 1.mysql登录命令

mysql -h ip -P 端口 -u 用户名 -p

##### 2.查看数据库版本

mysql --version/mysql -V

select version();

##### 3.常用命令

show databases;

use database_test;

show tables from database_test;

desc table_test;

show create table table_test;

select database(); 查看当前所在库

SHOW ENGINES; 查看当前mysql支持的存储引擎

SHOW VARIABLES; 查看系统变量及其值

SHOW VARIBALES like '%wait_timeout%t';

##### 4.mysql语法规范

1.不区分大小写,但建议关键字大写，表名与列名小写

2.每条命令最好用英文分号结尾

3.每条命令根据需要,可以进行缩进或换行

4.注释
  – 单行注释: #注释文字
  – 单行注释: -- 注释文字，注意这里需要加空格
  – 多行注释: /* 注释文字 */

##### 5.SQL语言分类

DQL(Data Query Language):数据查询语言 select相关语句
DML(Data Manipulate Language):数据操作语言 insert,update,delete语句
DDL(Data Define Languge):数据定义语言 create,drop,alter语句
TCL(Transaction Control Language):事务控制语言 set autocommit=0,start transaction,savepoint,commit,rollback

#### 二. MYSQL数据类型

##### 1.概览

整数类型: bit 、 bool 、 tinyint 、 smallint 、 mediumint 、 int 、 bigint
浮点数类型: float 、 double 、 decimal
字符串类型: char 、 varchar 、 tinyblob 、 blob 、 mediumblob 、 longblob 、tinytext 、 text 、 mediumtext 、 longtext
日期类型: Date 、 DateTime 、 TimeStamp 、 Time 、 Year
其他数据类型

##### 2.类型(n)说明

int(N)
• 无论N等于多少，int永远占4个字节
• N表示的是显示宽度,不足的用0补足,超过的无视长度而直接显示整个数字,但这要整型设置了unsigned zerofill才有效

##### 3.decimal采用的是四舍五入，[loat和double采用的是四舍六入五成双

就是5以下舍弃5以上进位,如果需要处理数字为5的时候,需要看5后面是否还有不为0的任何数字,如果有,则直接进位，如果没有,需要看5前面的数字，若是奇数则进位,若是偶数则将5舍掉

##### 4.char

char类型占用固定长度,如果存放的数据为固定长度的建议使用char类型,如:手机号码、身份证等固定长度的信息。

##### 5.数据类型选择

• 选小不选大:一般情况下选择可以正确存储数据的最小数据类型,越小的数据类型通常更快,占用磁盘,内存和CPU缓存更小。
• 简单就好:简单的数据类型的操作通常需要更少的CPU周期,例如:整型比字符操作代价要小得多,因为字符集和校对规则(排序规则)使字符比整型比较更加复杂。
• 尽量避免NULL:尽量制定列为NOT NULL,除非真的需要NULL类型的值,有NULL的列值会使得索引、索引统计和值比较更加复杂。
• 浮点类型的建议统一选择decimal
• 记录时间的建议使用int或者bigint类型,将时间转换为时间戳格式,如将时间转换为秒、毫秒,进行存储,方便走索引

#### 三.MYSQL常用管理命令

##### 1.创建用户

create user 用户名[@主机名] [identified by '密码'];

不指定主机名时,表示这个用户可以从任何主机连接mysql服务器

eg:create user 'test'@% identified by '123';

##### 2.修改密码

SET PASSWORD FOR '用户名'@'主机' = PASSWORD('密码');

create user 用户名[@主机名] [identified by '密码'];

use mysql;
update user set authentication_string = password('321') where user = 'test' and host = '%';
flush privileges;

老版本中密码字段是password

##### 3.给用户授权

grant privileges ON database.table TO 'username'[@'host'] [with grant option]

grant命令说明:
• priveleges (权限列表),可以是 all ,表示所有权限,也可以是 select、update 等权限,多个权限之间用逗号分开。
• ON 用来指定权限针对哪些库和表,格式为 数据库.表名 ,点号前面用来指定数据库名,点号后面用来指定表名, *.* 表示所有数据库所有表。
• TO 表示将权限赋予某个用户, 格式为 username@host，@前面为用户名,@后面接限制的主机,可以是IP段、域名以及%，%表示任何地方。
• WITH GRANT OPTION 这个选项表示该用户可以将自己拥有的权限授权给别人。注意:经常有人在创建操作用户的时候不指定WITH GRANT OPTION选项导致后来该用户不能使用GRANT命令创建用户或者给其它用户授权。 备注:可以使用GRANT重复给用户添加权限,权限叠加,比如你先给用户添加一个select权限,然后又给用户添加一个insert权限,那么该用户就同时拥有了select和insert权限。

eg:grant select(user,host) on mysql.user to 'test1'@'localhost';

说明:test1用户只能查询mysql.user表的user,host字段

##### 4.查看用户有那些权限

show grants for '用户名'[@'主机']

##### 5.撤销用户权限

revoke privileges ON database.table FROM '用户名'[@'主机'];

##### 6.删除用户

drop user '用户名'[@‘主机’]

delete from user where user='用户名' and host='主机';
flush privileges;

##### 7.授权原则

• 只授予能满足需要的最小权限,防止用户干坏事,比如用户只是需要查询,那就只给select权限就可以了,不要给用户赋予update、insert或者delete权限
• 创建用户的时候限制用户的登录主机,一般是限制成指定IP或者内网IP段
• 初始化数据库的时候删除没有密码的用户,安装完数据库的时候会自动创建一些用户,这些用户默认没有密码
• 为每个用户设置满足密码复杂度的密码
• 定期清理不需要的用户,回收权限或者删除用户

#### 四.DDL常见操作

##### 1.库的管理

创建库 create database [if not exists] 库名;

删除库 drop databases [if exists] 库名;

foreign key:为表中的字段设置外键
语法:foreign key(当前表的列名) references 引用的外键表(外键表中字段名称)

##### 2.表的管理

删除表 drop table [if exists] 表名;

修改表名 alter table 表名 rename [to] 新表名;
表设置备注 alter table 表名 comment '备注信息';
复制表
只复制表结构 create table 表名 like 被复制的表名;
复制表结构+数据 create table 表名 [as] select 字段,... from 被复制的表 [where 条件];

alter table 表名 add column 列名 类型 [列约束];

alter table 表名 modify column 列名 新类型 [约束];
alter table 表名 change column 列名 新列名 新类型 [约束];
2种方式区别:modify不能修改列名,change可以修改列名

alter table 表名 drop column 列名;

#### 五.DML常见操作

##### 1.插入

insert into 表名[(字段,字段)] values (值,值);
insert into 表名 set 字段 = 值,字段 = 值;

##### 2.更新

update 表名 [[as] 别名] set [别名.]字段 = 值,[别名.]字段 = 值 [where条件];
update 表1 [[as] 别名1],表名2 [[as] 别名2] set [别名.]字段 = 值,[别名.]字段 = 值[where条件]

##### 3.删除数据

delete [别名] from 表名 [[as] 别名] [where条件];

eg:
delete t1 from test1 t1,test2 t2 where t1.a=t2.c2;
删除test1表中的记录,条件是这些记录的字段a在test.c2中存在的记录

truncate 表名;

drop,truncate,delete区别
•drop (删除表):删除内容和定义,释放空间,简单来说就是把整个表去掉,以后要新增数据是不可能的,除非新增一个表。drop语句将删除表的结构被依赖的约束(constrain),触发器(trigger)索引(index),依赖于该表的存储过程/函数将被保留,但其状态会变为:invalid。
如果要删除表定义及其数据,请使用 drop table 语句。
•truncate (清空表中的数据):删除内容、释放空间但不删除定义(保留表的数据结构),与drop不同的是,只是清空表数据而已。
注意:truncate不能删除具体行数据,要删就要把整个表清空了。
•delete (删除表中的数据):delete 语句用于删除表中的行。delete语句执行删除的过程是每次从表中删除一行,并且同时将该行的删除操作作为事务记录在日志中保存,以便进行进行回滚操作。truncate与不带where的delete :只删除数据,而不删除表的结构(定义)。truncate table 删除表中的所有行,但表结构及其列、约束、索引等保持不变。
对于由foreign key约束引用的表,不能使用truncate table ,而应使用不带where子句的delete语句。由于truncate table 记录在日志中,所以它不能激活触发器。
delete语句是数据库操作语言(dml),这个操作会放到 rollback segement 中,事务提交之后才生效;如果有相应的 trigger,执行的时候将被触发。truncate、drop 是数据库定义语言(ddl),操作立即生效,原数据不放到 rollback segment 中,不能回滚,操作不触发 trigger。
如果有自增列,truncate方式删除之后,自增列的值会被初始化,delete方式要分情况(如果数据库被重启了,自增列值也会被初始化,数据库未被重启,则不变)
• 如果要删除表定义及其数据,请使用 drop table 语句
• 安全性:小心使用 drop 和 truncate,尤其没有备份的时候,否则哭都来不及
• 删除速度,一般来说: drop> truncate > delete

#### 六.select查询

##### 1.基本查询

1.建议别名前面跟上as关键字
2.查询数据的时候,避免使用select *,建议需要什么字段写什么字段

##### 2.
