package com.springbook.view.reply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springbook.biz.board.Criteria;
import com.springbook.biz.board.PageMaker;
import com.springbook.biz.board.ReplyService;
import com.springbook.biz.board.ReplyVO;

@RestController("replyController22")
@RequestMapping("/replies")
public class ReplyController {
	
	@Autowired
    private final ReplyService replyService;

   
    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }
    
    //댓글 등록 메서드
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody ReplyVO replyVO) {
        ResponseEntity<String> entity = null;
        try {
        	System.out.println(replyVO.toString());
            replyService.addReply(replyVO);
            entity = new ResponseEntity<>("regSuccess", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("에러");
            entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
    
    //댓글 목록 메드
    @RequestMapping(value = "/all/{articleNo}", method = RequestMethod.GET)
    public ResponseEntity<List<ReplyVO>> list(@PathVariable("articleNo") Integer articleNo) {
        ResponseEntity<List<ReplyVO>> entity = null;
        try {
            entity = new ResponseEntity<>(replyService.getReplies(articleNo), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
    
    //댓글 수정 메서드
    @RequestMapping(value = "/{replyNo}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<String> update(@PathVariable("replyNo") Integer replyNo,@RequestBody ReplyVO replyVO) {
        ResponseEntity<String> entity = null;
        try {
            replyVO.setReplyNo(replyNo);
            replyService.modifyReply(replyVO);
            entity = new ResponseEntity<>("modSuccess", HttpStatus.OK);
        } catch (Exception e) {
        	System.out.println("modify error");
            e.printStackTrace();
            entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
    
    //댓글 삭제 메서드
    @RequestMapping(value = "/{replyNo}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable("replyNo") Integer replyNo) {
        ResponseEntity<String> entity = null;
        try {
            replyService.removeReply(replyNo);

            System.out.println(replyNo);
            entity = new ResponseEntity<>("delSuccess", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return entity;
    }
    
    //댓글 목록 페이징 처리
    @RequestMapping(value = "/{articleNo}/{page}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> listPaging(@PathVariable("articleNo") Integer articleNo,
                                                          @PathVariable("page") Integer page) {

        ResponseEntity<Map<String, Object>> entity = null;

        try {

            Criteria criteria = new Criteria();
            criteria.setPage(page);
            criteria.setPerPageNum(page);
            System.out.println(criteria.getPerPageNum());

            List<ReplyVO> replies = replyService.getRepliesPaging(articleNo, criteria);
            int repliesCount = replyService.countReplies(articleNo);

            PageMaker pageMaker = new PageMaker();
            pageMaker.setCriteria(criteria);
            pageMaker.setTotalCount(repliesCount);

            Map<String, Object> map = new HashMap<>();
            map.put("replies", replies);
            map.put("pageMaker", pageMaker);

            entity = new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            entity = new ResponseEntity<>(HttpStatus.OK);

        }

        return entity;
    }

}
