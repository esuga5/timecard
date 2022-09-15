DROP TABLE IF EXISTS sections;

CREATE TABLE sections(
    id SERIAL NOT NULL,
    --部門シリアル(pk)
    code varchar(4),
    --部門コード
    section_name varchar(40),
    --部門名称
    PRIMARY KEY (id)
);

COMMENT ON COLUMN
    sections.id IS '部門シリアル(pk)';
COMMENT ON COLUMN
    sections.code IS '部門コード';
COMMENT ON COLUMN
    sections.section_name IS '部門名称';

INSERT INTO
    sections (code, section_name)
VALUES
    ('1000', '株式会社ジーンリバティー');

INSERT INTO
    sections (code, section_name)
VALUES
    ('2000', '株式会社ウェム');
