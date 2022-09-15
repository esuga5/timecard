DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL NOT NULL,
    --ユーザーシリアル
    username varchar(40) UNIQUE NOT NULL,
    --ユーザーid
    password varchar(64) NOT NULL,
    --ユーザーパスワード
    display_name varchar(40) NOT NULL,
    --ユーザー名
    section_code varchar(4),
    --ユーザー所属部門コード
    news varchar(100),
    --お知らせ
    joined_at timestamp NOT NULL,
    --入社日
    default_start_time integer DEFAULT 0,
    --デフォルト出勤時間
    default_end_time integer DEFAULT 0,
    --デフォルト退勤時間
    default_break_time integer DEFAULT 0,
    --デフォルト休憩時間
    admin_flag boolean,
    --管理者権限有無フラグ
    inactive_flag boolean,
    --管理終了フラグ
    created_at timestamp NOT NULL,
    --登録日時
    updated_at timestamp NOT NULL,
    --更新日時
    PRIMARY KEY(id)
);

COMMENT ON COLUMN
    users.id IS 'ユーザーシリアル';
COMMENT ON COLUMN
    users.username IS 'ユーザーid';
COMMENT ON COLUMN
    users.password IS 'ユーザーパスワード';
COMMENT ON COLUMN
    users.display_name IS 'ユーザー名';
COMMENT ON COLUMN
    users.section_code IS 'ユーザー所属部門コード';
COMMENT ON COLUMN
    users.news IS 'お知らせ';
COMMENT ON COLUMN
    users.joined_at IS '入社日';
COMMENT ON COLUMN
    users.default_start_time IS 'デフォルト出勤時間';
COMMENT ON COLUMN
    users.default_end_time IS 'デフォルト退勤時間';
COMMENT ON COLUMN
    users.default_break_time IS 'デフォルト休憩時間';
COMMENT ON COLUMN
    users.admin_flag IS '管理者権限有無フラグ';
COMMENT ON COLUMN
    users.inactive_flag IS '管理終了フラグ';
COMMENT ON COLUMN
    users.created_at IS '登録日時';
COMMENT ON COLUMN
    users.updated_at IS '更新日時';

-- -- 初回ログインユーザー登録
-- INSERT INTO
--     users (username, password, display_name, section_code, news, joined_at, default_start_time, default_end_time, default_break_time, admin_flag, inactive_flag, created_at, updated_at)
-- VALUES
--     ('test@test.com', '$2a$10$zsArKyB9iItQpKp2iOZ4wO9C2SeiFSxWrDJLBh2Q5CM1JYH6Lz0xG', 'testユーザー', '1000', '', '2020/05/08 14:48:52.610', 0, 0, 0, false, false, '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610');
