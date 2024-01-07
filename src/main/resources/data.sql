

INSERT INTO student(created_at, email, password) VALUES (now(), 'wjsalstjr59@gmail.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');
INSERT INTO student(created_at, email, password) VALUES (now(), 'riverboy_12@naver.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');
INSERT INTO student(created_at, email, password) VALUES (now(), 'dnwnalstjr@naver.com', '{bcrypt}$2a$10$y4GgwEP1Yj1CzEyN7ToAh.XRiXn7y.8m0I43Cp.v.rBbSs7cxwv86');

INSERT INTO department(name) VALUES ('소프트웨어학부');
INSERT INTO department(name) VALUES ('경영학부');
INSERT INTO department(name) VALUES ('간호대학');

INSERT INTO enrollment (student_id, department_id) VALUES (1, 1);
INSERT INTO enrollment (student_id, department_id) VALUES (1, 2);

INSERT INTO enrollment (student_id, department_id) VALUES (2, 1);

INSERT INTO enrollment (student_id, department_id) VALUES (3, 1);
INSERT INTO enrollment (student_id, department_id) VALUES (3, 2);
INSERT INTO enrollment (student_id, department_id) VALUES (3, 3);

INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES (now(), 1, 1, '링크', '제목', '작성자');
INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES (now(), 1, 2, '링크', '제목', '작성자');
INSERT INTO board (post_at, post_number, department_id, link, title, writer) VALUES (now(), 1, 3, '링크', '제목', '작성자');

