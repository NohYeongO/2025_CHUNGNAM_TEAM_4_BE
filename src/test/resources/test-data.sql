-- 테스트용 더미 데이터 (JPA 테이블 생성 후 자동 실행)

-- 사용자 더미 데이터
INSERT INTO user (nickname, email, password, role, point) VALUES
('테스트 사용자1', 'test1@example.com', 'password1', 'USER', 1000),
('테스트 사용자2', 'test2@example.com', 'password2', 'USER', 500),
('관리자', 'admin@example.com', 'adminpass', 'ADMIN', 2000);

INSERT INTO mission (title, description, type, status, reward_points) VALUES
('플라스틱 분리수거', '플라스틱 쓰레기를 올바르게 분리수거하세요', 'DAILY', 'ACTIVATE', 10),
('대중교통 이용하기', '자동차 대신 대중교통을 이용하세요', 'DAILY', 'ACTIVATE', 15),
('10Km 걷기', '대중교통 대신 10km를 걸어보세요', 'DAILY', 'ACTIVATE', 10),
('대중교통 이용하기', '자동차 대신 대중교통을 이용하세요', 'DAILY', 'ACTIVATE', 15),
('텀블러 사용하기', '일회용 컵 대신 텀블러를 사용하세요', 'WEEKLY', 'ACTIVATE', 20),
('에코백 사용하기', '일회용 비닐봉지 대신 에코백을 사용하세요', 'WEEKLY', 'ACTIVATE', 25);