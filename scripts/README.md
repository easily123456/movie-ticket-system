生成场次 SQL 的脚本

快速说明

- 脚本路径: scripts/generate_sessions.py
- 运行示例（Windows PowerShell）：
  python .\scripts\generate_sessions.py --start-date 2025-11-20 --days 7 --per-movie 4 > .\sql\more_sessions.sql

参数说明：

- --start-date: 起始日期（YYYY-MM-DD），默认 2025-11-20
- --days: 生成多少天（默认 7）
- --per-movie: 每部电影每天生成多少场次（默认 3）
- --movie-ids: 电影 id 范围或列表，默认 "1-16"
- --halls: 影厅 id 范围或列表，默认 "1-6"
- --seed: 随机种子，用于可重复生成（默认 42）

如何使用

1. 在项目根目录（包含 scripts）运行上面的示例命令，它会在 sql/more_sessions.sql 生成 INSERT 语句。
2. 你可以在本地用 MySQL 客户端/管理工具把生成的 SQL 导入数据库，或手动把内容追加到 sql/insert_base_data.sql 的 sessions 部分。

注意

- 脚本不检查影厅冲突（同一影厅同一时段是否有重叠），仅用于快速生成大量种子数据以便开发/测试。
- 如果你希望脚本按影片时长精确计算 end_time，并且使用数据库中真实的影片 duration，请告诉我，我可以把脚本扩展为读取 movies 表（需要数据库连接配置或将 movies durations 以 JSON 提供给脚本）。
