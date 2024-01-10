

INSERT INTO student(created_at, email, password) VALUES (now(), 'wjsalstjr59@gmail.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');
INSERT INTO student(created_at, email, password) VALUES (now(), 'riverboy_12@naver.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');
INSERT INTO student(created_at, email, password) VALUES (now(), 'dnwnalstjr@naver.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');

INSERT INTO department(name) VALUES ('소프트웨어학부');
INSERT INTO department(name) VALUES ('경영학부');
INSERT INTO department(name) VALUES ('간호학과');
INSERT INTO department(name) VALUES ('기계공학부');
INSERT INTO department(name) VALUES ('건축학부');
INSERT INTO department(name) VALUES ('융합공학부');
INSERT INTO department(name) VALUES ('첨단소재공학과');

INSERT INTO enrollment (student_id, department_id) VALUES (1, 1);
INSERT INTO enrollment (student_id, department_id) VALUES (1, 2);

INSERT INTO enrollment (student_id, department_id) VALUES (2, 1);

INSERT INTO enrollment (student_id, department_id) VALUES (3, 1);
INSERT INTO enrollment (student_id, department_id) VALUES (3, 2);
INSERT INTO enrollment (student_id, department_id) VALUES (3, 3);

INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (1, '2024-01-05', 2812, 1, 'https://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=', '2023학년도 동계계절학기 성적조회 안내', '학부사무실');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (2, '2024-01-08', 2813, 1, 'https://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=', '[첨단분야 혁신융합대학사업] POLARIS SIF 2024 행사 안내', '학부사무실');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (3, '2024-01-08', 2814, 1, 'https://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=', '2024학년도 1학기 재학생 등록기간 안내', '학부사무실');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (4, '2024-01-08', 2815, 1, 'https://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=', '2023-2학기 학기별 석차 조회 기간 안내', '학부사무실');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (5, '2024-01-05', 6811, 2, 'https://biz.cau.ac.kr/2016/sub06/sub06_01_view.php?bbsIdx=', '[서울과정] 국비지원 미국취업연수과정 6기 - 2024년도 3월 과정 예비자 사전 선발', null);
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (6, '2024-01-08', 6812, 2, 'https://biz.cau.ac.kr/2016/sub06/sub06_01_view.php?bbsIdx=', '2024학년도 1학기 등록금 분할납부 안내', null);
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (7, '2024-01-03', 2552, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '2024학년도 편입학 필기고사(1.7)관련 안내 및 협조 요청', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (8, '2024-01-03', 2553, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '[교직]복수전공 교직과정 이수예정자 선발 및 신청 안내', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (9, '2024-01-08', 2554, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '[학사] 2023-2학기 학기별 석차 조회 기간 안내', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (10, '2024-01-09', 2555, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '[졸업]2024년 2월 학부 졸업대상자 졸업사정 및 졸업유예 신청 관련 안내', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (11, '2024-01-09', 2556, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '[첨단분야 혁신융합대학사업] POLARIS SIF 2024 행사 안내', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (12, '2024-01-09', 2557, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '중앙대학교 적십자간호대학 한시계약직 채용', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (13, '2024-01-09', 2558, 3, 'https://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=', '[일반]2024학년도 적십자간호대학 학사력 공지', '관리자');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (14, '2024-01-09', 2816, 1, 'https://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=', '2024-01 캡스톤디자인(2) 공지', '학부사무실');
INSERT INTO board (id, post_at, post_number, department_id, link, title, writer) VALUES (15, '2024-01-09', 6813, 2, 'https://biz.cau.ac.kr/2016/sub06/sub06_01_view.php?bbsIdx=', '★ 2024년 2월 졸업사정(75회) 및 학부사무실 졸업사정 미접수기간 안내', null);


insert into board (post_at, post_number, department_id, link, title, writer)
values('2024-01-08', 4685, 4, 'https://me.cau.ac.kr/bbs/board.php?bo_table=sub5_1&wr_id=', '2024학년도 1학기 재학생 등록기간 안내', null);

insert into board (post_at, post_number, department_id, link, title, writer)
values('2023-11-06', 61, 4, 'https://me.cau.ac.kr/bbs/board.php?bo_table=sub5_6&wr_id=', '[★중요★] 2023-2 ABEEK 공학인증 정기상담 안내 (김태형 교수님 , ~ 11.08.(수)) [응답마감]', null);

insert into board (post_at, post_number, department_id, link, title, writer)
values('2024-01-04', 4648, 4, 'https://me.cau.ac.kr/bbs/board.php?bo_table=sub5_3&wr_id=', '[현대모비스] 2024 Mobility SW HACKATHON 참가 신청 (~ 01.21.일 23시)', null);

INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES ('2023-12-08', 1275, 5, 'http://archicau.com/wordpress/공지사항/?mod=document&uid=', '[서울특별시 건축기획과] 서울도시건축전시관 "서울의 내일을 만들다 - 차세대 건축가들의 집단 비전" 전시 개최', '건축학부');

INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES ('2024-01-08', 1489, 6, 'https://ie.cau.ac.kr/sch_5/notice.php?p_mode=view&p_idx=', '2023-2학기 학기별 석차 조회 기간 안내', null);

INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES ('2024-01-08', 289, 7, 'http://ame.cau.ac.kr/bbs/board.php?tbl=bbs45&mode=VIEW&num=', '2024학년도 1학기 재학생 등록기간 안내', '관리자');
