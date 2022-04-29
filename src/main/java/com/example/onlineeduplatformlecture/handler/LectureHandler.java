package com.example.onlineeduplatformlecture.handler;

import com.example.onlineeduplatformlecture.model.Content;
import com.example.onlineeduplatformlecture.model.Enrolment;
import com.example.onlineeduplatformlecture.model.Lecture;
import com.example.onlineeduplatformlecture.model.Matching;
import com.example.onlineeduplatformlecture.repository.ContentRepository;
import com.example.onlineeduplatformlecture.repository.LectureRepository;
import com.example.onlineeduplatformlecture.repository.MatchingRepository;
import com.example.onlineeduplatformlecture.model.Rating;
import com.example.onlineeduplatformlecture.model.Score;
import com.example.onlineeduplatformlecture.repository.ScoreRepository;
import com.example.onlineeduplatformlecture.repository.EnrolmentRepository;
import com.example.onlineeduplatformlecture.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class LectureHandler {

    private final LectureRepository lectureRepository;
    private final ContentRepository contentRepository;
    private final MatchingRepository matchingRepository;
    private final LectureService lectureService;
    private final EnrolmentService enrolmentService;
    private final ContentService contentService;
    private final ScoreService scoreService;
    private final EnrolmentRepository enrolmentRepository;
    private final LectureProducerService lectureProducerService;

    public LectureHandler(
            LectureRepository lectureRepository,
            ContentRepository contentRepository,
            MatchingRepository matchingRepository,
            EnrolmentRepository enrolmentRepository,
            LectureService lectureService,
            EnrolmentService enrolmentService,
            ContentService contentService,
            ScoreService scoreService,
            LectureProducerService lectureProducerService) {
        this.lectureRepository = lectureRepository;
        this.contentRepository = contentRepository;
        this.matchingRepository = matchingRepository;
        this.enrolmentRepository = enrolmentRepository;
        this.lectureService = lectureService;
        this.enrolmentService = enrolmentService;
        this.contentService = contentService;
        this.scoreService = scoreService;
        this.lectureProducerService = lectureProducerService;
    }

    @Autowired
    ScoreRepository scoreRepository;
    public Mono<ServerResponse> getLectureList(ServerRequest serverRequest){
        Flux<Lecture> lectureList = lectureService.getLectureList();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(lectureList, Lecture.class);
    }
    public Mono<ServerResponse> getLecture(ServerRequest serverRequest) {

        Long lectureId = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        System.out.println(lectureId);
        Mono<Lecture> lectureMono = lectureService.getLecture(lectureId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(lectureMono, Lecture.class)
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createLecture(ServerRequest request){
        Mono<Lecture> lectureMono = request.bodyToMono(Lecture.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
                });
        return lectureMono
                .flatMap(lecture -> {
                    System.out.println(lecture.getTitle());
                    System.out.println(lecture.getLocation());
                    lectureProducerService.sendMessage(lecture.getTitle(), lecture.getLocation());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.lectureRepository.save(lecture), Lecture.class);
                }).log();
    };
//    Mono<ServerResponse> changeExposeLecture(ServerRequest serverRequest);
//
    public Mono<ServerResponse> enrollLecture(ServerRequest serverRequest){
        Mono<Enrolment> enrolmentMono = serverRequest.bodyToMono(Enrolment.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
                });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                enrolmentMono.flatMap(this.enrolmentRepository::save), Enrolment.class);
    }
//
    public Mono<ServerResponse> matchTeacher(ServerRequest request){
        Mono<Matching> lectureMono = request.bodyToMono(Matching.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
                });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                lectureMono.flatMap(this.matchingRepository::save), Matching.class);
    };
//
    public Mono<ServerResponse> getContentList(ServerRequest serverRequest){

        Long lectureId  = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Flux<Content> contentFlux = contentService.getContentList(lectureId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contentFlux, Content.class)
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> uploadContent(ServerRequest request){
        Mono<Content> contentMono = request.bodyToMono(Content.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
                });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                contentMono.flatMap(this.contentRepository::save), Content.class);
    }

    public Mono<ServerResponse> getContent(ServerRequest serverRequest){

        Long lectureId  = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Long contentId  = Long.parseLong(serverRequest.pathVariable("contentId"));

        System.out.println(lectureId);
        System.out.println(contentId);
        Mono<Content> contentMono = contentService.getContent(lectureId,contentId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contentMono, Content.class)
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("데이터 오류"));

    }

    public Mono<ServerResponse> getScore(ServerRequest serverRequest) {
        int lectureId = Integer.parseInt(serverRequest.pathVariable("lectureId"));
        Mono<Score> scoreMono = scoreRepository.findScoreById(lectureId,1);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        scoreMono.subscribe(System.out::println);
        return scoreMono
                .flatMap(score -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(scoreMono, Score.class))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> setScore(ServerRequest serverRequest) {
        Mono<Score> scoreMono = serverRequest.bodyToMono(Score.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
        });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                scoreMono.flatMap(this.scoreRepository::save), Score.class);
    }

    public Mono<ServerResponse> changeExposeLecture(ServerRequest serverRequest) {
//        Lecture paramLecture = serverRequest.bodyToMono(Lecture.class).block();
//        Lecture lecture = lectureRepository.getById((long) paramLecture.getLectureId());
//        lecture.setExposedYn(1);
//        lectureRepository.saveAndFlush(lecture);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Lecture(), Lecture.class)
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("데이터 오류"));
    }
//
//    Mono<ServerResponse> getScore(ServerRequest serverRequest);
//    Mono<ServerResponse> setScore(ServerRequest serverRequest);

//    public Mono<ServerResponse> getRatingList(ServerRequest serverRequest){
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fromObject(ratingService.getRatingList()));
//    };
//
//    public Mono<ServerResponse> getRating(ServerRequest serverRequest){
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ratingService.getRating());
//    };
//    public Mono<ServerResponse> setRating(ServerRequest serverRequest){
//        Rating rating = serverRequest.bodyToMono(Rating.class).block();
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ratingService.saveRating(rating));
//    };

}
