package project.gym.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.entity.BoardEntity;
import project.gym.member.entity.UserEntity;
import project.gym.member.repository.UserRepository;
import project.gym.member.service.MemberService;

@Controller

public class BoardController {



    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberService memberService;

    @GetMapping("/board/write") //localhost:8090/board/write
    public String boardWriteForm(Model model) {

        return "boardwrite";
    }

    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/board/writepro")
    public String boardWritePro(BoardEntity board, Model model, @RequestParam(value="file")MultipartFile file) throws Exception{

        memberService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/list") //관리자 모드에서 잘올라갔나보기
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        Page<BoardEntity> list = null;


        if(searchKeyword == null) {
            list = memberService.boardList(pageable);
        }else {
            list = memberService.boardSearchList(searchKeyword, pageable);
        }


        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        //model.addAttribute("imageUrls",imageUrls);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1 관리자모드에서만
    public String boardView(Model model, @RequestParam("id") Integer id, HttpSession session) {
        BoardEntity board = memberService.boardView(id);
        String imageUrl=board.getFilePath();

        String formattedContent = board.getContent().replace("\n", "<br/>");
        model.addAttribute("formattedContent", formattedContent);
        model.addAttribute("board", board);
        model.addAttribute("imageUrl",imageUrl);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", authentication);

        String username = (String) session.getAttribute("loginUsername");

        // 기본값으로 loggedIn 변수를 false로 설정
        model.addAttribute("loggedIn", false);

        if (username != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("username", username);

            // 사용자 정보를 조회해서 추가
            UserEntity user = userRepository.findByUsername(username);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("board", board);
                model.addAttribute("imageUrl",imageUrl);
                model.addAttribute("formattedContent", formattedContent);
            } else {
                model.addAttribute("error", "User not found");
            }
        }
        return "boardview";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,
                              Model model) {

        model.addAttribute("board", memberService.boardView(id));

        return "boardmodify";
    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, BoardEntity board,
                              @RequestParam("file") MultipartFile file) throws Exception{

        BoardEntity boardTemp = memberService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setFileName(board.getFileName());
        memberService.write(boardTemp, file);


        return "redirect:/board/list";

    }
    @CrossOrigin(origins = "https://www.priqma.com")
    @PostMapping("/board/delete/{id}")
    public String boardDelete(@PathVariable("id") Integer id){

     memberService.deleteBoard(id);

        return "redirect:/board/list";

    }
}