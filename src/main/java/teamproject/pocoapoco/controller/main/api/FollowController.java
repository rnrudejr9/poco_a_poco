package teamproject.pocoapoco.controller.main.api;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import teamproject.pocoapoco.domain.dto.follow.FollowingResponse;
import teamproject.pocoapoco.domain.dto.response.Response;
import teamproject.pocoapoco.domain.entity.Follow;
import teamproject.pocoapoco.service.FollowService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/social")
public class FollowController {

    private final FollowService followService;
    @ApiOperation(value = "follow", notes = "해당 유저를 follow/unfollow 합니다.")
    @PostMapping("/{userId}/follow")
    public Response<?> follow(@PathVariable Long userId, Authentication authentication){

     return Response.success(followService.follow(authentication.getName(),userId));
    }
//    @ApiOperation(value = "unfollow", notes = "해당 유저를 unfollow 합니다.")
//    @DeleteMapping("/{userId}/unfollow")
//    public Response<?> unFollow(@PathVariable Long userId, Authentication authentication){
//        return Response.success(followService.unFollow(authentication.getName(), userId));
//    }


    @ApiOperation(value = "팔로잉 리스트", notes = "해당 유저가 팔로잉 하고있는 유저의 명단을 표시합니다.")
    @GetMapping("/{userId}/following")
    public Response<Page<FollowingResponse>> getFollowingList(@PathVariable Long userId , Pageable pageable){

        return Response.success(followService.getFollowingList(pageable,userId));
    }
    @ApiOperation(value = "팔로워 리스트", notes = "해당 유저를 팔로잉 하고있는 유저의 명단을 표시합니다.")
    @GetMapping("/{userId}/follower")
    public Response<Page<FollowingResponse>> getFollowedList(@PathVariable Long userId , Pageable pageable){

        return Response.success(followService.getFollowedList(pageable,userId));
    }

    @ApiOperation(value = "팔로워 수", notes = "해당 유저를 팔로우 하고있는 유저의 수를 표시합니다.")
    @GetMapping("/{userId}/followedCount")
    public Response<?> followedCount(@PathVariable Long userId){

        return Response.success(followService.followedCount(userId));
    }
    @ApiOperation(value = "팔로잉 수", notes = "해당 유저가 팔로우 하고있는 유저의 수를 표시합니다.")
    @GetMapping("/{userId}/followingCount")
    public Response<?> followingCount(@PathVariable Long userId){

        return Response.success(followService.followingCount(userId));
    }
}
