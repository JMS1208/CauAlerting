package com.jms.alertmessaging.service.alert;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.jms.alertmessaging.repository.post.PostRepository;
import com.jms.alertmessaging.repository.student.StudentRepository;
import com.jms.alertmessaging.service.crawl.CrawlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlertingService {

    private static final Logger logger = LoggerFactory.getLogger(AlertingService.class);

    private static final String username_TITLE = "중앙대 소프트웨어학부 공지사항에 새글이 등록되었습니다";


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CrawlingService crawlingService;

    @Autowired
    private EmailSender usernameSender;

    //주기적으로 크롤링해서 메시지 보내고 디비에 저장하기
//    @Scheduled(fixedRate = 5000000)
    public void repeatCrawlingAndMessaging() {
        try  {
            //디비에서 최근에 전송한 Post 가져오기
            Optional<Board> recentPostOptional = postRepository.findRecentPost();

            //최근 게시글 리스트 가져오기
            List<Board> boardListToCrawl = crawlingService.crawlPostList();

            //디비에 최근에 전송한 Post 가 있는 경우 - 필터링
            if(recentPostOptional.isPresent()) {
                Board recentBoard = recentPostOptional.get();

                //크롤링 할 애들
                List<Board> filteredPostListEntity = new ArrayList<>();

                for(Board board : boardListToCrawl) {
                    //디비에 저장된 것보다 커야함
                    if(board.postNumber > recentBoard.postNumber) {
                        boardListToCrawl.add(board);
                    }
                }

                //갈아 끼움
                boardListToCrawl = filteredPostListEntity;
            }

            //알림 보낼 게시글
            List<Board> boardListToAlert = new ArrayList<>();

            for(Board boardToCrawl : boardListToCrawl) {
                Board board = crawlingService.crawlPost(boardToCrawl.postNumber);
                boardListToAlert.add(board);
            }

            logger.info("알림 보낼 게시글: " + boardListToAlert);

            //보낼 게시글이 없으면 종료
            if(boardListToAlert.isEmpty()) {
                return;
            }


            //알림 보낼 사람들
            List<Student> studentEntities = studentRepository.findAllUserList();

            //보낼 사람이 없으면 종료
            if(studentEntities.isEmpty()) {
                return;
            }

            //스트림 데이터를 병렬로 처리
            studentEntities.parallelStream().forEach((user)-> {
                //@Async 로 이메일을 병렬로 보냄
                usernameSender.sendEmail(user.getUsername(), username_TITLE, boardListToAlert.toString());
            });

            //이메일 보내고 디비에 저장
            postRepository.savePostList(boardListToAlert);

            logger.info(boardListToAlert.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}