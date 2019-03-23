> 项目中对MySQL插入数据的时候，出现错误信息，查阅网上相关文章后解决，记录之，下次供参考。
### 错误提示一：MySQLIntegrityConstraintViolationException

```sql
### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Unknown error 1048
### The error may involve com.tyron.task.dao.RegisterMapper.addRegister-Inline
### The error occurred while setting parameters
```
**错误原因**：在插入数据时，数据库指定字段非空，但是插入了空内容，导致出错。
**解决方法**：
- 将字段设置为可为空
- 将字段设值后再插入，进行非空判断

### 错误提示二：MysqlDataTruncation
```sql
Cause: com.mysql.jdbc.MysqlDataTruncation: Data truncation: #22001
```
**错误原因**：插入的数据字段长度超过了数据库字段的限定长度
**解决方案**：适当扩大数据库字段长度

**以上错误均出于实际生产环境，可能我们报错的内容相同，但是出错的原因不同，以上的解决方案仅供参考，如能对你有帮助，灰常荣幸。**
附上：[mysql 错误代码大全](https://www.cnblogs.com/JimCalark/p/7808575.html)
