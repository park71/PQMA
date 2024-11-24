package project.gym.member.controller;

import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import project.gym.member.service.GoogleSheetsService;
import project.gym.member.service.MariaDBService;

public class GoogleMemberController extends HttpServlet {
    @Autowired
    private GoogleSheetsService googleSheetsService;
    @Autowired
    private MariaDBService mariaDBService;
 // 구글에서 회원권리트스로 데이터가져오기
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String phone = request.getParameter("phone");
//
//        try {
//            List<Object> memberData = googleSheetsService.findMemberByPhone(phone);
//            if (memberData != null) {
//                mariaDBService.insertMember(memberData);
//                response.getWriter().write("회원 정보가 성공적으로 가져와졌습니다.");
//            } else {
//                response.getWriter().write("해당 전화번호로 회원을 찾을 수 없습니다.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.getWriter().write("오류가 발생했습니다.");
//        }
//    }
}
