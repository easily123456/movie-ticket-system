DROP DATABASE IF EXISTS movie_ticket_system;
		-- 创建数据库
CREATE DATABASE IF NOT EXISTS movie_ticket_system 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE movie_ticket_system;

-- 删除已存在的表（按依赖关系逆序）
DROP TABLE IF EXISTS system_config;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS halls;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;

-- 创建表（按依赖关系顺序）
-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role ENUM('ADMIN', 'USER') DEFAULT 'USER' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 电影类型表
CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '类型ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '类型名称',
    description TEXT COMMENT '类型描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影类型表';

-- 资讯表
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资讯ID',
    title VARCHAR(200) NOT NULL COMMENT '资讯标题',
    content TEXT NOT NULL COMMENT '资讯内容',
    cover_image VARCHAR(255) COMMENT '封面图片',
    author VARCHAR(50) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶(0:否,1:是)',
    status TINYINT DEFAULT 1 COMMENT '状态(0:草稿,1:发布)',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯表';

-- 电影表
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '电影ID',
    title VARCHAR(200) NOT NULL COMMENT '电影标题',
    original_title VARCHAR(200) COMMENT '原始标题',
    genre_id BIGINT NOT NULL COMMENT '类型ID',
    duration INT COMMENT '时长(分钟)',
    director VARCHAR(100) COMMENT '导演',
    actors TEXT COMMENT '演员列表',
    release_date DATE COMMENT '上映日期',
    country VARCHAR(50) COMMENT '国家/地区',
    language VARCHAR(50) COMMENT '语言',
    description TEXT COMMENT '电影描述',
    poster_url VARCHAR(255) COMMENT '海报URL',
    trailer_url VARCHAR(255) COMMENT '预告片URL',
    rating DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
    vote_count INT DEFAULT 0 COMMENT '评分人数',
    price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门(0:否,1:是)',
    status TINYINT DEFAULT 1 COMMENT '状态(0:下架,1:上架)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影表';

-- 放映厅表
CREATE TABLE halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '放映厅ID',
    name VARCHAR(100) NOT NULL COMMENT '放映厅名称',
    capacity INT NOT NULL COMMENT '容量',
    seat_layout TEXT COMMENT '座位布局(JSON格式)',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='放映厅表';

-- 场次表
CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '场次ID',
    movie_id BIGINT NOT NULL COMMENT '电影ID',
    hall_id BIGINT NOT NULL COMMENT '放映厅ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    price DECIMAL(10,2) NOT NULL COMMENT '实际价格',
    available_seats INT NOT NULL COMMENT '可用座位数',
    booked_seats INT DEFAULT 0 COMMENT '已预订座位数',
    status TINYINT DEFAULT 1 COMMENT '状态(0:取消,1:正常)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场次表';

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    seat_numbers VARCHAR(500) NOT NULL COMMENT '座位号(JSON数组)',
    seat_count INT NOT NULL COMMENT '座位数量',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status ENUM('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING' COMMENT '订单状态',
    pay_time DATETIME COMMENT '支付时间',
    cancel_time DATETIME COMMENT '取消时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 评论表
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    movie_id BIGINT NOT NULL COMMENT '电影ID',
    content TEXT NOT NULL COMMENT '评论内容',
    rating DECIMAL(2,1) NOT NULL COMMENT '评分(0.0-10.0)',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态(0:删除,1:正常)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 收藏表
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    movie_id BIGINT NOT NULL COMMENT '电影ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 系统配置表
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';


USE movie_ticket_system;

-- 清空所有表数据（按依赖关系逆序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE system_config;
TRUNCATE TABLE favorites;
TRUNCATE TABLE comments;
TRUNCATE TABLE orders;
TRUNCATE TABLE sessions;
TRUNCATE TABLE halls;
TRUNCATE TABLE movies;
TRUNCATE TABLE news;
TRUNCATE TABLE genres;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 插入管理员用户 (密码: admin123)
INSERT INTO users (username, password, email, phone, avatar, role, status, last_login_time) VALUES
('admin', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'admin@cinema.com', '13800138000', '/avatars/admin.jpg', 'ADMIN', 1, '2024-01-14 09:00:00'),
('user1', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'user1@email.com', '13800138001', '/avatars/user1.jpg', 'USER', 1, '2024-01-14 10:30:00'),
('user2', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'user2@email.com', '13800138002', '/avatars/user2.jpg', 'USER', 1, '2024-01-14 11:15:00'),
('user3', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'user3@email.com', '13800138003', '/avatars/user3.jpg', 'USER', 1, '2024-01-14 14:20:00'),
('user4', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'user4@email.com', '13800138004', '/avatars/user4.jpg', 'USER', 1, '2024-01-14 16:45:00'),
('user5', '$2a$10$ysC9h5LYys7QDfYPIbe4kOmDE1MCMWEpRmxRqGq8LtN3lPBOat1Qm', 'user5@email.com', '13800138005', '/avatars/user5.jpg', 'USER', 1, '2024-01-14 18:30:00');

-- 插入电影类型
INSERT INTO genres (name, description, sort_order) VALUES
('动作', '包含大量动作场面的电影，通常有激烈的打斗、追逐和爆炸场景', 1),
('喜剧', '以幽默和搞笑为主的电影，旨在让观众发笑和放松', 2),
('爱情', '以爱情故事为主题的电影，关注人物之间的感情发展', 3),
('科幻', '基于科学幻想的电影，常涉及未来科技、太空探索等主题', 4),
('恐怖', '以恐怖和惊悚为主题的电影，旨在引起观众的恐惧感', 5),
('剧情', '以人物情感和故事发展为主的电影，注重情节和人物塑造', 6),
('动画', '动画制作的电影，适合各年龄段观众', 7),
('悬疑', '充满悬念和谜团的电影，让观众猜测剧情发展', 8),
('冒险', '以冒险旅程为主题的电影，常涉及探索未知领域', 9),
('奇幻', '包含魔法、神话等超自然元素的电影', 10);

-- 插入电影数据
INSERT INTO movies (title, original_title, genre_id, duration, director, actors, release_date, country, language, description, poster_url, trailer_url, price, is_hot, rating, vote_count) VALUES
('流浪地球2', 'The Wandering Earth 2', 4, 173, '郭帆', '吴京,刘德华,李雪健,沙溢,宁理,王智,朱颜曼滋', '2023-01-22', '中国', '汉语', '在并不遥远的未来，太阳急速衰老与膨胀，再过几百年整个太阳系将被它吞噬毁灭。为了应对这场史无前例的危机，地球各国放下芥蒂，成立联合政府，试图寻找人类存续的出路。通过摸索与考量，最终推着地球逃出太阳系的“移山计划”获得压倒性胜利。人们着手建造上万台巨大的行星发动机，带着地球踏上漫漫征程。满腔赤诚的刘培强（吴京 饰）和韩朵朵（王智 饰）历经层层考验成为航天员大队的一员，并由此相知相恋。但是漫漫征途的前方，仿佛有一股神秘的力量不断破坏者人类的自救计划。看似渺小的刘培强、量子科学家图恒宇（刘德华 饰）、联合政府中国代表周喆直（李雪健 饰）以及无数平凡的地球人，构成了这项伟大计划的重要一环……
　　本片根据刘慈欣同名科幻小说改编', '/posters/wandering-earth2.jpg', '/trailers/wandering-earth2.mp4', 45.00, 1, 8.3, 125000),
('满江红', 'Full River Red', 2, 159, '张艺谋', '沈腾,易烊千玺,张译,雷佳音,岳云鹏,王佳怡', '2023-01-22', '中国', '汉语', '南宋绍兴年间，一代忠良岳鹏举物故，引无数良臣赤子扼腕叹息。四年后，奸相秦桧（雷佳音 饰）率兵与金国相约会谈。谁知会谈前夜，金国使者在秦桧驻地为人所害。事态紧急，亲兵营副统领孙均（易烊千玺 饰）得知不成器的老舅——小兵张大（沈腾 饰）了解线索，遂将其带到秦桧处。秦桧命二人一个时辰内找出真凶，否则一律处死。时间飞速流逝，舞姬瑶琴（王佳怡 饰）、更夫丁三旺（潘斌龙 饰）、马夫刘喜（余皑磊 饰）接连卷入其中，而宰相府总管何立（张译 饰）与副总管武义淳（岳云鹏 饰）相继入场，又将这场波谲云诡的调查涂满血腥。
　　谁是真正凶手？他们行刺所为何事？奸相内心深处又藏着什么不可告人的秘密？', '/posters/full-river-red.jpg', '/trailers/full-river-red.mp4', 42.00, 1, 7.2, 98000),
('深海', 'Deep Sea', 7, 112, '田晓鹏', '苏鑫,王亭文,滕奎兴,杨婷,吉静,郭浩然', '2023-01-22', '中国', '汉语', '父母离异，家庭重组，亲情缺失。小女孩参宿（王亭文 配音）怯怯懦懦，脸上鲜有笑容。父亲（滕奎兴 配音）现在一心扑在新的妻子和出生不久的小弟弟身上，对她充满敷衍。远在他乡的母亲似乎开启了新的生活，拒绝对这个女儿施以哪怕一丁点的爱的恩惠。阴晦的日子里，参宿随同家人登上了六天七夜的海上游轮。狂风暴雨，怒浪滔天，女孩悲伤的心情被不断放大。当那团有如乱麻一般的龙卷风出现在海面时，母亲的歌声从远处传来，而参宿也消失在无尽的夜色之中。当她再度醒来时，发现自己身处海底的某个奇怪地方，光怪陆离的深海大饭店内，奇形怪状的食客们一边抱怨一边大快朵颐。而唯利是图的老板南河（苏鑫 配音）则自顾自做着他引以为豪的所谓创新菜。
　　参宿的到来，给南河带来了无穷的混乱与麻烦……', '/posters/deep-sea.jpg', '/trailers/deep-sea.mp4', 38.00, 0, 7.4, 45000),
('无名', 'Hidden Blade', 6, 128, '程耳', '梁朝伟,王一博,周迅,黄磊,森博之,大鹏', '2023-01-22', '中国', '汉语', '20世纪40年代，上海正处于孤岛时期。各方势力犬牙交错，他们有姓无名，从不同的立场影响着历史走向。设在租界的76号特务机关，是听命于汪伪政府并服务日本帝国主义的邪恶机构。76号的主任何先生（梁朝伟 饰）巧舌如簧，粉饰太平，周游于日本特务机关长渡部（森博之 饰）和投机分子唐部长（大鹏 饰）中间，而他的微笑背后似乎别有深意。特务人员王先生（王传君 饰）和叶先生（王一博 饰）是一对听命于政治强权的好友，几具日本人的尸体，让他们的命运开始转向。承受不住革命的重压和死亡的威胁，原地下党成员张先生（黄磊 饰）突然叛变，他的变节使诡局走向激化。
　　命运多舛，弃生向死，向死而生！', '/posters/hidden-blade.jpg', '/trailers/hidden-blade.mp4', 40.00, 0, 6.7, 32000),
('阿凡达：水之道', 'Avatar: The Way of Water', 4, 192, '詹姆斯·卡梅隆', '萨姆·沃辛顿,佐伊·索尔达娜,西格妮·韦弗,史蒂芬·朗', '2022-12-16', '美国', '英语', '影片设定在《阿凡达》的剧情落幕十余年后，讲述了萨利一家（杰克、奈蒂莉和孩子们）的故事：危机未曾消散，一家人拼尽全力彼此守护、奋力求生，并历经艰险磨难。
　　杰克和奈蒂莉组建了家庭，他们的孩子也逐渐成长，为这个家庭带来了许多欢乐。然而危机未曾消散，萨利一家拼尽全力彼此守护、奋力求生，最终来到潘多拉星球临海的岛礁族寻求庇护。岛礁族首领特诺瓦里与罗娜尔为萨利一家提供了庇护所，这个部族的成员都是天生的潜水好手，也和海洋中的各种生物建立了密切联系。', '/posters/avatar2.jpg', '/trailers/avatar2.mp4', 55.00, 1, 8.0, 89000),
('速度与激情10', 'Fast X', 1, 141, '路易斯·莱特里尔', '范·迪塞尔,米歇尔·罗德里格兹,杰森·莫玛,布丽·拉尔森', '2023-05-17', '美国', '英语', '在完成了无数任务，克服了各种不可能的困难之后，唐老大（范·迪塞尔 Vin Diesel 饰）和他的家族以智慧、勇气和速度过五关斩六将，打败了一路上的所有敌人。如今，他们面对的是一名危险至极的对手：这个从过往阴影中浮出水面的具有致命威胁的人物，誓要报偿血海深仇，决心破坏这个家族，彻底摧毁唐老大所热爱的一切及其至亲至爱之人。', '/posters/fast10.jpg', '/trailers/fast10.mp4', 48.00, 1, 6.9, 45000),
('铃芽之旅', 'Suzume', 7, 122, '新海诚', '原菜乃华,松村北斗,深津绘里,染谷将太', '2023-03-24', '日本', '日语', '宁静的九州乡间小镇，生活着平凡的少女岩户铃芽（原菜乃华 配音）。这天上学路上，她邂逅了神秘的白衣青年宗像草太（松村北斗 配音）。草太的言行举止引起了铃芽的好奇，她随着这个青年来到了一处废墟，并看到了一扇在水中央突兀伫立的门。懵懵懂懂，铃芽无意间解开了某个古老封印。在此之 后，一场地震突发，而偏偏只有铃芽才能看见地震前发生的诡异现象。随后这个惶恐的女孩才发现，草太原来是有着古老传承的闭门师，他巡游日本各地正是为了阻止更大的灾难发生。
　　从西日本到东日本，追逐灾难的旅途中，铃芽尘封的记忆也渐渐苏醒……', '/posters/suzume.jpg', '/trailers/suzume.mp4', 42.00, 0, 7.3, 38000),
('保你平安', 'Post-Truth', 2, 112, '大鹏', '大鹏,李雪琴,尹正,王迅,王圣迪', '2023-03-10', '中国', '汉语', '人过中年的魏平安（董成鹏 饰）半生潦倒，一文不名。当年为朋友出头锒铛入狱，妻子也带着女儿改嫁他人。而今他干起墓地推销工作，虽然拮据，幸而乐观。这一天，他得知消息，自己曾经的客户韩露（宋茜 饰）要被强制迁出现在的墓位，理由是有传言她生前曾是一名风尘女子，而力主将她赶走的正是担心脏了自家风水的财团老板冯总（马丽 饰）。平安在韩露生前与之接触较多，深信这是一个善良纯洁的女子，为此他想方设法要还这个可怜女孩以清白。与此同时，关于韩露的谣言愈演愈烈，网络上每个人都成为道德审判家，每个人又不屑他人的名誉与清白。
　　渺小卑微的平安，为了自己所认为的“正确的事”，与一双双无形的黑手展开较量……', '/posters/post-truth.jpg', '/trailers/post-truth.mp4', 39.00, 0, 7.7, 28000),
('封神第一部', 'Creation of the Gods I', 10, 148, '乌尔善', '费翔,李雪健,黄渤,于适,陈牧驰,此沙', '2023-07-20', '中国', '汉语', '天寒地冻，杀气逼人。大商二王子殷寿（费翔 饰）带领亲手调教的质子旅和殷商大军征讨叛乱的冀州侯苏护，却无意间解除了轩辕坟中妖狐的封印。妖狐附身苏护之女妲己（娜然 饰）的身上，被殷寿带回朝歌献给父王和王兄。夜宴之上，大王子智乱神迷，拔剑弑父。在此之后，殷寿继承王位，而天降灾异又迫使他做出自焚祭天的决定。与此同时，昆仑仙人姜子牙（黄渤 饰）携封神榜下山，寻找天下共主，以期救拔苍生。在朝歌期间，他觉察到殷寿的残暴，遂匆匆逃离。另一方面，曾视殷寿为明主的王子殷郊（陈牧驰 饰）以及西伯侯质子姬发（于适 饰）也发现了商王的私欲和妲己的诡异之处。
　　阴云密布的朝歌，即将掀起一场血雨腥风……"封神榜"下山，寻找天下共主，以救苍生。西伯侯之子姬发逐渐发现殷寿的本来面目，反出朝歌……', '/posters/creation-gods.jpg', '/trailers/creation-gods.mp4', 46.00, 1, 7.8, 68000),
('八角笼中', 'Never Say Never', 6, 117, '王宝强', '王宝强,陈永胜,史彭元,王迅,张祎曈', '2023-07-06', '中国', '汉语', '貌不惊人的沙厂老板向腾辉（王宝强 饰）曾经是一名技术精湛、潜力无限的格斗选手，但因听信教练的话吃了违禁药品遭到禁赛，之后更因伤人锒铛入狱。从此他远离擂台，离年少时的梦想越来越远。偶然机缘，向腾辉在江湖骗子的帮助下重新接触格斗，成立了一个少儿格斗俱乐部。他跑到偏远山区找来了野蛮生长打架抢劫的未成年儿童，教他们打拳挣钱，更为赚钱不择手段打假拳。而在这狼奔豕突的岁月里，向腾辉从孩子们的身上看到了自己当初的影子，他更不愿意少年们重新过回偷鸡摸狗的绝望日子。为此，他义无反顾带着少年走上了与命运抗争的八角擂台……
　　本片以四川凉山“格斗孤儿”的真实事件为蓝本改编。"残忍、血腥"的画面刺激了不明真相的人们的神经。一夜之间，舆论开始发酵。向腾辉的生活、孩子们的前途都陷入到人们以善良为名编织的大网中，让他们难以挣脱，重回泥沼，关于未来，他们的"出路"又将在哪……', '/posters/never-say-never.jpg', '/trailers/never-say-never.mp4', 41.00, 0, 7.5, 42000),
('消失的她', 'Lost in the Stars', 8, 122, '崔睿,刘翔', '朱一龙,倪妮,文咏珊,杜江', '2023-06-22', '中国', '汉语', '东南亚某国，年轻男子何非（朱一龙 饰）疯狂寻找失踪的妻子李木子，然而当地警局视若罔闻，令他出离愤怒。可就在某个清晨，何非突然发现身边躺着一名陌生女子，更令他毛骨悚然的是，女子居然声称是他的妻子李木子（文咏珊 饰）。慌乱之余，何找来华人警察郑成（杜江 饰），试图揭穿假冒者的骗局，谁知种种迹象表明，对面的陌生人真实不虚。隐隐之中，何非认为妻子被犯罪团伙绑架，他只得求助干练的金牌律师陈麦（倪妮 饰）追索真相。在调查中过程中，细微线索逐渐浮出水面，而更加黑暗的真相也开始暴露在阳光之下……
　　本片改编自前苏联电影《为单身汉设下的陷阱》。', '/posters/lost-in-stars.jpg', '/trailers/lost-in-stars.mp4', 43.00, 1, 6.4, 95000),
('孤注一掷', 'No More Bets', 6, 130, '申奥', '张艺兴,金晨,咏梅,王传君,王大陆,周也', '2023-08-08', '中国', '汉语', '南亚某国，由于当地政府监管不力以及有意纵容，诈骗产业极其昌盛，炒股、挖币、网游、电商、博彩等，各种手段层出不穷，花样百出。某个科技产业园内，诈骗团伙头目陆经理（王传君 饰）张狂傲慢，用诡计和暴力统治着这个血泪王国。程序员潘生（张艺兴 饰）离开之前的公司，根据高薪招聘启示 走入了陆经理设下的圈套，成为一名利用网络诈骗的狗推。因为羡慕闺蜜的生活，模特梁安娜（金晨 饰）也飞到异国他乡寻找机会，结果护照被没收，成为陆经理手下利用美色诈骗的工具。进入人间炼狱，仿佛再也见不到活着出去的希望……
　　电影取材自上万起真实诈骗案例。', '/posters/no-more-bets.jpg', '/trailers/no-more-bets.mp4', 44.00, 1, 6.9, 87000),
('奥本海默', 'Oppenheimer', 6, 180, '克里斯托弗·诺兰', '基里安·墨菲,艾米莉·布朗特,马特·达蒙,小罗伯特·唐尼', '2023-08-30', '美国', '英语', '影片聚焦"原子弹之父"当我们为权力金钱焦虑、兴奋与愤怒时，却根本想象不到“他们”在谈论着怎样更重要的事情。
　　随着战争阴云笼罩世界上空，各国紧锣密鼓抓紧军事竞赛。为了抢占先机，美国陆军中将莱斯利·格罗夫斯（马特·达蒙 Matt Damon 饰）找到量子力学与核物理学领域的扛鼎人物罗伯特·奥本海默（基里安·墨菲 Cillian Murphy 饰），力荐其担任曼哈顿计划的首席科学家以及洛斯阿拉莫斯国家实验室的总负责人。经过两年争分夺秒的研发，硕大的蘑菇云终于在荒原的上空腾起，也宣告着绞肉机一般的二战即将落下帷幕。奥本海默有如将火种带到人间的普罗米修斯，可是对人性的参悟和对未来的担忧迫使他走向与政府相悖的道路。更可悲的是，凡人钟情的物欲也将一世天才裹挟至炼狱之中，永世燃烧……', '/posters/oppenheimer.jpg', '/trailers/oppenheimer.mp4', 52.00, 1, 8.8, 105000),
('芭比', 'Barbie', 2, 114, '格蕾塔·葛韦格', '玛格特·罗比,瑞恩·高斯林,亚美莉卡·费雷拉,凯特·麦克金农', '2023-07-21', '美国', '英语', '在梦幻浪漫的芭比乐园里，美丽自信的芭比们每日过着快乐无忧的生活，她们活跃在每一个工作岗位，肯是点缀，女孩的聚会才至关重要。直到某一天，我们的主角芭比（玛格特·罗比 Margot Robbie 饰）突然提到了生死，她的完美生活毫无征兆发生改变。她的口气不再清新，面容开始犹豫憔悴，也无法轻盈地从顶楼飘落下来。经前辈点播，她意识到人类世界某个人和她建立着联系，而那个人的心境似乎正投射到芭比的身上来。
　　为了找回曾经的完美生活，芭比和肯（瑞恩·高斯林 Ryan Gosling 饰）辗转来到现实世界，并经过一番波折见到了葛洛莉娅（亚美莉卡·费雷拉 America Ferrera 饰）母女，而肯也在见识到男权社会的魅力之后，心中升起要改变芭比乐园的念头…… ', '/posters/barbie.jpg', '/trailers/barbie.mp4', 48.00, 0, 7.8, 78000),
('银河护卫队3', 'Guardians of the Galaxy Vol. 3', 4, 150, '詹姆斯·古恩', '克里斯·帕拉特,佐伊·索尔达娜,戴夫·巴蒂斯塔,凯伦·吉兰', '2023-05-05', '美国', '英语', '历经磨难与考验，银河护卫队总算在虚无之地暂时安顿下来。可就在某一天，不速之客亚当术士（威尔·保尔特 Will Poulter 饰）的闯入不仅打破了这里的安宁与祥和，更将一场生死危机带到这群好朋友中间。战斗中，火箭浣熊（布莱德利·库珀 Bradley Cooper 配音） 身负重伤，奄奄一息。为了拯救好友的生命，星爵（克里斯·帕拉特 Chris Pratt 饰）和星云（凯伦·吉兰 Karen Gillan饰）、毁灭者（戴夫·巴蒂斯塔 Dave Bautista 饰）、螳螂女（庞·克莱门捷夫 Pom Klementieff 饰）、格鲁特（范·迪塞尔 Vin Diesel 饰）卡魔拉（佐伊·索尔达娜 Zoe Saldana 饰）前往创造了火箭的奥果公司寻找起死回生的办法。
　　然而在那个诡异的世界，他们意外发现了火箭浣熊不为人知的悲惨过往…… ', '/posters/guardians3.jpg', '/trailers/guardians3.mp4', 50.00, 0, 8.2, 65000),
('蜘蛛侠：纵横宇宙', 'Spider-Man: Across the Spider-Verse', 7, 140, '乔伊姆·多斯·桑托斯', '沙梅克·摩尔,海莉·斯坦菲尔德,奥斯卡·伊萨克', '2023-06-02', '美国', '英语', '地球-65，蜘蛛女格温（海莉·斯坦菲尔德 Hailee Steinfeld 配音）因为好友彼特之死而充满愧疚，而父亲对蜘蛛侠的误解也让她倍感焦虑。某次行动中，格温结识了来自其他宇宙的同伴米圭尔·奥哈拉和杰西卡·德鲁。从他们口中得知，为了防止多元宇宙进一步崩坏，米圭尔策划成立了由蜘蛛侠中的精英组成的突击队。格温受邀加入他们，并利用时空穿梭的机会来到了地球-1610，见到久违的迈尔斯（沙梅克·摩尔 Shameik Moore 配音）。此时的迈尔斯和父母关系紧张，他憧憬格温，渴望加入突击队又遭到拒绝。
　　随后迈尔斯又听到一个可怕的消息，每一个宇宙中，失去至亲正是所有蜘蛛侠的可悲宿命……', '/posters/spiderman-across.jpg', '/trailers/spiderman-across.mp4', 47.00, 0, 8.6, 72000);

-- 插入放映厅数据
INSERT INTO halls (name, capacity, seat_layout, status) VALUES
('1号激光厅', 120, '{"rows":10,"cols":12,"disabledSeats":["A1","A12","J1","J12"],"vipSeats":["D5","D6","D7","D8","E5","E6","E7","E8"]}', 1),
('2号IMAX厅', 200, '{"rows":10,"cols":20,"disabledSeats":["A1","A20","J1","J20"],"vipSeats":["E8","E9","E10","E11","E12","F8","F9","F10","F11","F12"]}', 1),
('3号杜比厅', 80, '{"rows":8,"cols":10,"disabledSeats":["A1","A10","H1","H10"],"vipSeats":["D4","D5","D6","D7","E4","E5","E6","E7"]}', 1),
('4号VIP厅', 40, '{"rows":5,"cols":8,"disabledSeats":[],"vipSeats":["A1","A2","A3","A4","A5","A6","A7","A8","B1","B2","B3","B4","B5","B6","B7","B8"]}', 1),
('5号4DX厅', 60, '{"rows":6,"cols":10,"disabledSeats":["A1","A10","F1","F10"],"vipSeats":["C4","C5","C6","C7","D4","D5","D6","D7"]}', 1),
('6号巨幕厅', 150, '{"rows":10,"cols":15,"disabledSeats":["A1","A15","J1","J15"],"vipSeats":["E6","E7","E8","E9","E10","F6","F7","F8","F9","F10"]}', 1);


-- 插入场次数据（扩展更多场次）
INSERT INTO sessions (movie_id, hall_id, start_time, end_time, price, available_seats, booked_seats, status) VALUES
-- 流浪地球2 场次
(1, 1, '2024-01-15 10:00:00', '2024-01-15 12:53:00', 45.00, 116, 4, 1),
(1, 2, '2024-01-15 13:00:00', '2024-01-15 15:53:00', 55.00, 196, 4, 1),
(1, 6, '2024-01-15 16:30:00', '2024-01-15 19:23:00', 50.00, 146, 4, 1),
(1, 2, '2024-01-15 19:30:00', '2024-01-15 22:23:00', 60.00, 196, 4, 1),

-- 满江红 场次
(2, 1, '2024-01-15 14:00:00', '2024-01-15 16:39:00', 42.00, 116, 3, 1),
(2, 3, '2024-01-15 17:00:00', '2024-01-15 19:39:00', 45.00, 76, 4, 1),
(2, 1, '2024-01-15 20:00:00', '2024-01-15 22:39:00', 48.00, 116, 4, 1),

-- 深海 场次
(3, 3, '2024-01-15 16:00:00', '2024-01-15 17:52:00', 38.00, 76, 4, 1),
(3, 5, '2024-01-15 19:00:00', '2024-01-15 20:52:00', 42.00, 56, 4, 1),

-- 无名 场次
(4, 4, '2024-01-15 19:00:00', '2024-01-15 21:08:00', 60.00, 40, 2, 1),
(4, 3, '2024-01-15 21:30:00', '2024-01-15 23:38:00', 50.00, 76, 4, 1),

-- 阿凡达：水之道 场次
(5, 2, '2024-01-15 09:30:00', '2024-01-15 12:42:00', 55.00, 196, 6, 1),
(5, 6, '2024-01-15 14:00:00', '2024-01-15 17:12:00', 58.00, 146, 4, 1),
(5, 2, '2024-01-15 18:30:00', '2024-01-15 21:42:00', 65.00, 196, 4, 1),

-- 速度与激情10 场次
(6, 1, '2024-01-15 11:30:00', '2024-01-15 13:51:00', 48.00, 116, 5, 1),
(6, 5, '2024-01-15 15:00:00', '2024-01-15 17:21:00', 52.00, 56, 4, 1),
(6, 1, '2024-01-15 18:00:00', '2024-01-15 20:21:00', 55.00, 116, 4, 1),

-- 铃芽之旅 场次
(7, 3, '2024-01-15 13:30:00', '2024-01-15 15:32:00', 42.00, 76, 3, 1),
(7, 5, '2024-01-15 16:30:00', '2024-01-15 18:32:00', 45.00, 56, 4, 1),

-- 保你平安 场次
(8, 4, '2024-01-15 14:30:00', '2024-01-15 16:22:00', 39.00, 40, 2, 1),
(8, 3, '2024-01-15 17:00:00', '2024-01-15 18:52:00', 42.00, 76, 4, 1),

-- 1月16日场次
(1, 2, '2024-01-16 10:00:00', '2024-01-16 12:53:00', 45.00, 196, 4, 1),
(2, 1, '2024-01-16 14:00:00', '2024-01-16 16:39:00', 42.00, 116, 3, 1),
(5, 6, '2024-01-16 16:30:00', '2024-01-16 19:42:00', 58.00, 146, 4, 1),
(9, 3, '2024-01-16 19:00:00', '2024-01-16 21:28:00', 46.00, 76, 4, 1),
(10, 4, '2024-01-16 21:30:00', '2024-01-16 23:27:00', 41.00, 40, 2, 1),

-- 1月17日场次
(3, 5, '2024-01-17 10:30:00', '2024-01-17 12:22:00', 38.00, 56, 4, 1),
(4, 3, '2024-01-17 13:00:00', '2024-01-17 15:08:00', 40.00, 76, 4, 1),
(6, 1, '2024-01-17 15:30:00', '2024-01-17 17:51:00', 48.00, 116, 5, 1),
(11, 2, '2024-01-17 18:00:00', '2024-01-17 20:02:00', 43.00, 196, 4, 1),
(12, 6, '2024-01-17 20:30:00', '2024-01-17 22:40:00', 44.00, 146, 4, 1),

-- 1月18日场次
(7, 4, '2024-01-18 11:00:00', '2024-01-18 13:02:00', 42.00, 40, 2, 1),
(8, 3, '2024-01-18 13:30:00', '2024-01-18 15:22:00', 39.00, 76, 4, 1),
(13, 2, '2024-01-18 16:00:00', '2024-01-18 19:00:00', 52.00, 196, 6, 1),
(14, 1, '2024-01-18 19:30:00', '2024-01-18 21:24:00', 48.00, 116, 4, 1),
(15, 5, '2024-01-18 21:30:00', '2024-01-18 23:50:00', 50.00, 56, 4, 1),

-- 1月19日场次（周末增加场次）
(1, 6, '2024-01-19 09:30:00', '2024-01-19 12:23:00', 50.00, 146, 4, 1),
(1, 2, '2024-01-19 13:00:00', '2024-01-19 15:53:00', 55.00, 196, 4, 1),
(1, 1, '2024-01-19 16:30:00', '2024-01-19 19:23:00', 50.00, 116, 4, 1),
(1, 2, '2024-01-19 20:00:00', '2024-01-19 22:53:00', 60.00, 196, 4, 1),
(5, 6, '2024-01-19 10:00:00', '2024-01-19 13:12:00', 58.00, 146, 4, 1),
(5, 2, '2024-01-19 14:00:00', '2024-01-19 17:12:00', 65.00, 196, 4, 1),
(5, 6, '2024-01-19 18:00:00', '2024-01-19 21:12:00', 65.00, 146, 4, 1),
(13, 2, '2024-01-19 11:30:00', '2024-01-19 14:30:00', 52.00, 196, 6, 1),
(13, 1, '2024-01-19 15:30:00', '2024-01-19 18:30:00', 55.00, 116, 4, 1),
(13, 2, '2024-01-19 19:30:00', '2024-01-19 22:30:00', 60.00, 196, 4, 1),

-- 1月20日场次（周末增加场次）
(2, 1, '2024-01-20 10:00:00', '2024-01-20 12:39:00', 42.00, 116, 3, 1),
(2, 3, '2024-01-20 13:30:00', '2024-01-20 16:09:00', 45.00, 76, 4, 1),
(2, 1, '2024-01-20 16:30:00', '2024-01-20 19:09:00', 48.00, 116, 4, 1),
(2, 6, '2024-01-20 19:30:00', '2024-01-20 22:09:00', 50.00, 146, 4, 1),
(11, 3, '2024-01-20 11:00:00', '2024-01-20 13:02:00', 43.00, 76, 4, 1),
(11, 5, '2024-01-20 14:00:00', '2024-01-20 16:02:00', 46.00, 56, 4, 1),
(11, 3, '2024-01-20 17:00:00', '2024-01-20 19:02:00', 46.00, 76, 4, 1),
(12, 4, '2024-01-20 13:00:00', '2024-01-20 15:10:00', 44.00, 40, 2, 1),
(12, 1, '2024-01-20 16:00:00', '2024-01-20 18:10:00', 46.00, 116, 4, 1),
(12, 3, '2024-01-20 19:00:00', '2024-01-20 21:10:00', 48.00, 76, 4, 1);




-- 插入订单数据
INSERT INTO orders (order_no, user_id, session_id, seat_numbers, seat_count, total_price, status, pay_time, cancel_time) VALUES
('ORDER202401150001', 2, 1, '["B5","B6"]', 2, 90.00, 'PAID', '2024-01-14 15:30:00', NULL),
('ORDER202401150002', 3, 2, '["C7"]', 1, 55.00, 'PAID', '2024-01-14 16:45:00', NULL),
('ORDER202401150003', 4, 5, '["D3","D4"]', 2, 84.00, 'PAID', '2024-01-14 17:20:00', NULL),
('ORDER202401150004', 5, 8, '["E5"]', 1, 38.00, 'PAID', '2024-01-14 18:10:00', NULL),
('ORDER202401150005', 6, 12, '["F8","F9","F10"]', 3, 165.00, 'PAID', '2024-01-14 19:30:00', NULL),
('ORDER202401150006', 2, 15, '["C4","C5"]', 2, 96.00, 'PENDING', NULL, NULL),
('ORDER202401150007', 3, 18, '["B2"]', 1, 60.00, 'PAID', '2024-01-14 20:15:00', NULL),
('ORDER202401150008', 4, 20, '["D6","D7"]', 2, 78.00, 'CANCELLED', NULL, NULL),
('ORDER202401160001', 2, 21, '["C5","C6"]', 2, 90.00, 'PAID', '2024-01-15 14:20:00', NULL),
('ORDER202401160002', 3, 22, '["D7","D8"]', 2, 84.00, 'PAID', '2024-01-15 15:30:00', NULL),
('ORDER202401160003', 4, 23, '["E9","E10","E11"]', 3, 174.00, 'PAID', '2024-01-15 16:45:00', NULL),
('ORDER202401160004', 5, 24, '["B3"]', 1, 46.00, 'PAID', '2024-01-15 18:10:00', NULL),
('ORDER202401160005', 6, 25, '["A4","A5"]', 2, 82.00, 'PAID', '2024-01-15 19:25:00', NULL),
('ORDER202401170001', 2, 26, '["F4"]', 1, 38.00, 'PAID', '2024-01-16 10:15:00', NULL),
('ORDER202401170002', 3, 27, '["G5","G6"]', 2, 80.00, 'PAID', '2024-01-16 11:30:00', NULL),
('ORDER202401170003', 4, 28, '["H7"]', 1, 48.00, 'CANCELLED', NULL, '2024-01-16 12:45:00'),
('ORDER202401170004', 5, 29, '["C8","C9","C10"]', 3, 129.00, 'PAID', '2024-01-16 14:20:00', NULL),
('ORDER202401170005', 6, 30, '["D11","D12"]', 2, 88.00, 'PAID', '2024-01-16 15:35:00', NULL),
('ORDER202401180001', 2, 31, '["E2"]', 1, 42.00, 'PAID', '2024-01-17 09:45:00', NULL),
('ORDER202401180002', 3, 32, '["F3","F4"]', 2, 78.00, 'PAID', '2024-01-17 11:00:00', NULL),
('ORDER202401180003', 4, 33, '["G5"]', 1, 52.00, 'PENDING', NULL, NULL),
('ORDER202401180004', 5, 34, '["H6","H7"]', 2, 96.00, 'PAID', '2024-01-17 13:30:00', NULL),
('ORDER202401180005', 6, 35, '["A8","A9","A10"]', 3, 150.00, 'PAID', '2024-01-17 14:45:00', NULL),
('ORDER202401190001', 2, 36, '["B5","B6","B7"]', 3, 150.00, 'PAID', '2024-01-18 16:20:00', NULL),
('ORDER202401190002', 3, 37, '["C8"]', 1, 55.00, 'PAID', '2024-01-18 17:35:00', NULL),
('ORDER202401190003', 4, 38, '["D9","D10"]', 2, 116.00, 'PAID', '2024-01-18 18:50:00', NULL),
('ORDER202401190004', 5, 39, '["E11"]', 1, 65.00, 'PAID', '2024-01-18 20:05:00', NULL),
('ORDER202401190005', 6, 40, '["F12","F13"]', 2, 130.00, 'PAID', '2024-01-18 21:20:00', NULL),
('ORDER202401190006', 2, 41, '["G7","G8"]', 2, 104.00, 'PAID', '2024-01-18 22:35:00', NULL),
('ORDER202401190007', 3, 42, '["H9"]', 1, 52.00, 'REFUNDED', '2024-01-18 23:50:00', '2024-01-19 10:30:00'),
('ORDER202401190008', 4, 43, '["A10","A11"]', 2, 110.00, 'PAID', '2024-01-19 08:15:00', NULL),
('ORDER202401190009', 5, 44, '["B12"]', 1, 60.00, 'PAID', '2024-01-19 09:30:00', NULL);

-- 插入评论数据
INSERT INTO comments (user_id, movie_id, content, rating, like_count) VALUES
(2, 1, '特效震撼，剧情紧凑，中国科幻的又一力作！', 4.5, 12),
(3, 1, '比第一部更加精彩，人物塑造更加立体', 4.0, 8),
(4, 1, '刘德华的表演很出色，情感戏份很打动人', 4.2, 15),
(2, 2, '张艺谋导演的功力依旧，悬疑与喜剧结合得很好', 3.5, 5),
(3, 2, '沈腾和易烊千玺的搭档很有化学反应', 4.0, 7),
(5, 3, '画面太美了，每一帧都可以当壁纸', 4.5, 20),
(6, 3, '故事情节有些薄弱，但视觉效果绝对一流', 3.8, 6),
(2, 4, '梁朝伟的表演一如既往的稳', 4.0, 9),
(4, 5, '阿凡达的视觉效果依然领先业界', 4.8, 25),
(5, 6, '速度与激情系列还是那么刺激', 3.5, 4),
(6, 7, '新海诚的又一力作，画面和音乐都很棒', 4.3, 18),
(3, 8, '大鹏的导演功力越来越好了，笑中带泪', 4.1, 11),
(5, 9, '封神榜的故事重新演绎，特效很震撼，费翔的商王很有气势', 4.0, 8),
(6, 9, '中国神话大片的突破，期待第二部', 4.2, 12),
(2, 10, '王宝强的导演作品进步很大，很真实很感人', 4.5, 15),
(3, 10, '关注弱势群体的好电影，值得一看', 4.3, 9),
(4, 11, '悬疑感十足，反转再反转，朱一龙演技炸裂', 4.1, 20),
(5, 11, '剧情紧凑，倪妮的律师形象很帅气', 3.8, 7),
(6, 12, '反诈题材很有意义，王传君的表演印象深刻', 4.0, 18),
(2, 12, '教育意义大于娱乐性，适合带家人一起看', 4.2, 11),
(3, 13, '诺兰的又一神作，原子弹爆炸的场面震撼心灵', 4.9, 35),
(4, 13, '三个小时的电影一点都不觉得长，基里安的表演完美', 4.8, 28),
(5, 14, '芭比不仅仅是儿童电影，有很多深刻的社会隐喻', 4.0, 16),
(6, 14, '色彩缤纷的视觉盛宴，高斯林的肯太有趣了', 4.3, 14),
(2, 15, '银河护卫队完美收官，火箭浣熊的故事很催泪', 4.5, 22),
(3, 15, '漫威最近最好的作品，音乐和画面都很棒', 4.4, 19),
(4, 16, '动画电影的巅峰之作，视觉效果和故事都很出色', 4.7, 25),
(5, 16, '多元宇宙的概念玩出了新高度，期待下一部', 4.6, 21);

-- 插入收藏数据
INSERT INTO favorites (user_id, movie_id) VALUES
(2, 1),
(2, 3),
(2, 5),
(3, 2),
(3, 7),
(4, 1),
(4, 4),
(4, 8),
(5, 3),
(5, 6),
(6, 5),
(6, 7),
(2, 9),
(2, 13),
(3, 10),
(3, 14),
(4, 11),
(4, 15),
(5, 12),
(5, 16),
(6, 9),
(6, 13),
(2, 11),
(3, 15),
(4, 9),
(5, 13),
(6, 10);

-- 插入资讯数据
INSERT INTO news (title, content, cover_image, author, view_count, is_top, status, publish_time) VALUES
('春节档电影票房突破50亿', '今年春节档电影市场表现强劲，总票房已突破50亿元大关。《流浪地球2》《满江红》等影片表现突出，为电影市场注入强心剂。业内人士表示，这标志着中国电影市场的全面复苏。', '/news/spring-festival-boxoffice.jpg', '影院小编', 1250, 1, 1, '2024-01-10 09:00:00'),
('新片《热辣滚烫》定档大年初一', '由贾玲执导的新片《热辣滚烫》正式定档2024年大年初一。该片讲述了一个女孩通过拳击找到自我价值的故事，预计将成为春节档的有力竞争者。', '/news/hot-and-spicy.jpg', '影院小编', 890, 0, 1, '2024-01-08 14:30:00'),
('IMAX与杜比影院有什么区别？', '很多观众在选择观影格式时会有疑问：IMAX和杜比影院到底哪个更好？本文从屏幕大小、音响系统、画面技术等方面详细对比两种格式，帮助您做出更好的选择。', '/news/imax-vs-dolby.jpg', '技术达人', 1560, 0, 1, '2024-01-05 10:15:00'),
('会员卡全新升级，享更多优惠', '即日起，星光影城会员卡全新升级！新老会员可享受购票8折优惠、免费爆米花、生日特权等多项福利。详情请咨询前台或关注官方公众号。', '/news/member-card.jpg', '营销部', 780, 1, 1, '2024-01-12 16:45:00'),
('2024年最受期待的好莱坞大片', '从《沙丘2》到《碟中谍8》，2024年将有多部好莱坞大片上映。本文为您盘点今年最值得期待的10部国际影片，让您提前做好观影计划。', '/news/2024-hollywood.jpg', '电影爱好者', 2100, 0, 1, '2024-01-03 11:20:00'),
('儿童观影优惠政策调整通知', '为更好地服务家庭观众，星光影城对儿童观影政策进行调整：3岁以下儿童可免费观影（不占座），3-12岁儿童享受半价优惠。具体以影院现场公示为准。', '/news/child-policy.jpg', '客服中心', 650, 0, 1, '2024-01-15 14:00:00'),
('IMAX厅全新升级，观影体验再升级', '为提供更优质的观影体验，星光影城对IMAX厅进行了全面升级，包括新的投影系统、音响设备和座椅。升级后的IMAX厅将带给观众更加沉浸式的观影感受。', '/news/imax-upgrade.jpg', '技术部', 1120, 1, 1, '2024-01-13 10:00:00'),
('春节档预售即将开启，多部大片争锋', '2024年春节档电影预售将于1月25日正式开启，《热辣滚烫》《第二十条》《飞驰人生2》等多部大片将展开激烈角逐。提前购票享优惠！', '/news/spring-presale.jpg', '市场部', 980, 1, 1, '2024-01-14 14:30:00'),
('电影幕后：特效制作的那些事儿', '从《流浪地球2》到《封神第一部》，中国电影特效技术正在飞速发展。本文带您了解电影特效制作的幕后故事，揭秘那些震撼画面的诞生过程。', '/news/vfx-making.jpg', '电影迷', 750, 0, 1, '2024-01-11 11:15:00'),
('会员专属活动：导演见面会来袭', '本周六下午，星光影城将举办《八角笼中》导演王宝强见面会，会员可通过积分兑换参与名额。与导演零距离交流，机会难得！', '/news/director-meet.jpg', '活动部', 620, 0, 1, '2024-01-16 16:20:00'),
('儿童友好场次新增，带娃观影更轻松', '为方便家庭观众，星光影城新增周末上午儿童友好场次，允许家长携带婴幼儿入场，并适当调低音量、调亮灯光，营造更适合儿童的观影环境。', '/news/kid-friendly.jpg', '客服部', 530, 0, 1, '2024-01-12 13:45:00'),
('经典重映：《阿凡达》3D版再度归来', '为庆祝《阿凡达：水之道》上映一周年，星光影城将于本周末重映《阿凡达》3D版，让观众重温这部划时代的科幻巨作。', '/news/avatar-rerelease.jpg', '节目部', 890, 0, 1, '2024-01-15 15:10:00'),
('电影美食套餐更新，更多选择更优惠', '星光影城小卖部推出全新美食套餐，包括爆米花、饮料、热狗等多种组合，会员享受8折优惠。详情请咨询前台。', '/news/food-combo.jpg', '餐饮部', 480, 0, 1, '2024-01-17 12:30:00'),
('无障碍设施完善，关爱特殊群体', '星光影城已完成无障碍设施升级，包括轮椅专用座位、无障碍卫生间、手语导览等服务，为特殊群体提供更好的观影体验。', '/news/accessibility.jpg', '工程部', 410, 0, 1, '2024-01-18 14:00:00');

-- 插入系统配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('cinema_name', '星光影城', '影院名称'),
('cinema_address', '北京市朝阳区某某路123号', '影院地址'),
('cinema_phone', '400-123-4567', '客服电话'),
('booking_timeout', '15', '订单超时时间(分钟)'),
('refund_deadline', '30', '退款截止时间(分钟)'),
('business_hours', '09:00-24:00', '营业时间'),
('advance_booking_days', '7', '可提前预订天数'),
('min_booking_seats', '1', '最少预订座位数'),
('max_booking_seats', '6', '最多预订座位数'),
('student_discount', '0.8', '学生折扣'),
('member_discount', '0.85', '会员折扣'),
('vip_room_price_add', '20', 'VIP厅加价金额'),
('student_verification_required', '1', '是否需要学生验证'),
('max_refund_times_per_month', '3', '每月最大退款次数'),
('points_per_ticket', '10', '每张票获得积分'),
('points_to_money_ratio', '100', '积分兑换现金比例(100积分=1元)'),
('birthday_double_points', '1', '生日当天双倍积分'),
('weekday_discount', '0.9', '工作日折扣'),
('morning_show_discount', '0.8', '早场折扣'),
('late_show_discount', '0.85', '晚场折扣'),
('coupon_expire_days', '30', '优惠券有效期(天)'),
('auto_cancel_unpaid_minutes', '15', '自动取消未支付订单时间(分钟)');