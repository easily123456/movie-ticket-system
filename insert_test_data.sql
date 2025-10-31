USE movie_ticket_system;

-- 插入管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiC', 'admin@cinema.com', '13800138000', 'ADMIN', 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiC', 'user1@email.com', '13800138001', 'USER', 1),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiC', 'user2@email.com', '13800138002', 'USER', 1);

-- 插入电影类型
INSERT INTO genres (name, description, sort_order) VALUES
('动作', '包含大量动作场面的电影', 1),
('喜剧', '以幽默和搞笑为主的电影', 2),
('爱情', '以爱情故事为主题的电影', 3),
('科幻', '基于科学幻想的电影', 4),
('恐怖', '以恐怖和惊悚为主题的电影', 5),
('剧情', '以人物情感和故事发展为主的电影', 6),
('动画', '动画制作的电影', 7);

-- 插入电影数据
INSERT INTO movies (title, original_title, genre_id, duration, director, actors, release_date, country, language, description, poster_url, price, is_hot, rating, vote_count) VALUES
('流浪地球2', 'The Wandering Earth 2', 4, 173, '郭帆', '吴京,刘德华,李雪健', '2023-01-22', '中国', '汉语', '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。', '/posters/wandering-earth2.jpg', 45.00, 1, 8.3, 125000),
('满江红', 'Full River Red', 2, 159, '张艺谋', '沈腾,易烊千玺,张译', '2023-01-22', '中国', '汉语', '南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。会谈前夜，金国使者死在宰相驻地，所携密信也不翼而飞。一个小兵与亲兵营副统领机缘巧合被裹挟进这巨大阴谋之中，宰相秦桧命两人限一个时辰之内找到凶手。', '/posters/full-river-red.jpg', 42.00, 1, 7.2, 98000),
('深海', 'Deep Sea', 7, 112, '田晓鹏', '苏鑫,王亭文', '2023-01-22', '中国', '汉语', '在大海的最深处，藏着所有秘密。一位现代少女（参宿）误入梦幻的深海世界，却因此邂逅了一段独特的生命旅程。', '/posters/deep-sea.jpg', 38.00, 0, 7.4, 45000),
('无名', 'Hidden Blade', 6, 128, '程耳', '梁朝伟,王一博,周迅', '2023-01-22', '中国', '汉语', '1941年12月7日，日本偷袭珍珠港。次日，汪伪政府跟随日本对英美宣战，日本象征性占领上海全境。随着太平洋战争全面爆发，中国抗战形势也因此完全改变。中共地下党员冒险送出情报，破坏日蒋媾和，维护祖国。', '/posters/hidden-blade.jpg', 40.00, 0, 6.7, 32000);

-- 插入放映厅数据
INSERT INTO halls (name, capacity, seat_layout) VALUES
('1号激光厅', 120, '{"rows":10,"cols":12,"disabledSeats":["A1","A12","J1","J12"]}'),
('2号IMAX厅', 200, '{"rows":10,"cols":20,"disabledSeats":["A1","A20","J1","J20"]}'),
('3号杜比厅', 80, '{"rows":8,"cols":10,"disabledSeats":["A1","A10","H1","H10"]}'),
('4号VIP厅', 40, '{"rows":5,"cols":8,"disabledSeats":[]}');

-- 插入场次数据
INSERT INTO sessions (movie_id, hall_id, start_time, end_time, price, available_seats) VALUES
(1, 1, '2024-01-15 10:00:00', '2024-01-15 12:53:00', 45.00, 116),
(1, 2, '2024-01-15 13:00:00', '2024-01-15 15:53:00', 55.00, 196),
(2, 1, '2024-01-15 14:00:00', '2024-01-15 16:39:00', 42.00, 116),
(3, 3, '2024-01-15 16:00:00', '2024-01-15 17:52:00', 38.00, 76),
(4, 4, '2024-01-15 19:00:00', '2024-01-15 21:08:00', 60.00, 40);

-- 插入订单数据
INSERT INTO orders (order_no, user_id, session_id, seat_numbers, seat_count, total_price, status, pay_time) VALUES
('ORDER202401150001', 2, 1, '["B5","B6"]', 2, 90.00, 'PAID', '2024-01-14 15:30:00'),
('ORDER202401150002', 3, 2, '["C7"]', 1, 55.00, 'PAID', '2024-01-14 16:45:00');

-- 插入评论数据
INSERT INTO comments (user_id, movie_id, content, rating, like_count) VALUES
(2, 1, '特效震撼，剧情紧凑，中国科幻的又一力作！', 4.5, 12),
(3, 1, '比第一部更加精彩，人物塑造更加立体', 4.0, 8),
(2, 2, '张艺谋导演的功力依旧，悬疑与喜剧结合得很好', 3.5, 5);

-- 插入收藏数据
INSERT INTO favorites (user_id, movie_id) VALUES
(2, 1),
(2, 3),
(3, 2);

-- 插入资讯数据
INSERT INTO news (title, content, author, view_count, is_top, status, publish_time) VALUES
('春节档电影票房突破50亿', '今年春节档电影市场表现强劲，总票房已突破50亿元大关...', '影院小编', 1250, 1, 1, '2024-01-10 09:00:00'),
('新片《热辣滚烫》定档大年初一', '由贾玲执导的新片《热辣滚烫》正式定档2024年大年初一...', '影院小编', 890, 0, 1, '2024-01-08 14:30:00');

-- 插入系统配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('cinema_name', '星光影城', '影院名称'),
('cinema_address', '北京市朝阳区某某路123号', '影院地址'),
('cinema_phone', '400-123-4567', '客服电话'),
('booking_timeout', '15', '订单超时时间(分钟)'),
('refund_deadline', '30', '退款截止时间(分钟)');