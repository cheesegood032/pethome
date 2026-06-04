package com.pethome.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.model.Pet;
import com.pethome.service.PetService;
import com.pethome.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/pet/*")
public class PetServlet extends HttpServlet {
    private PetService petService = new PetService();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/add".equals(pathInfo)) {
                addPet(req, resp);
            } else if ("/update".equals(pathInfo)) {
                updatePet(req, resp);
            } else if ("/delete".equals(pathInfo)) {
                deletePet(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        resp.setContentType("application/json;charset=UTF-8");

        try {
            if ("/list".equals(pathInfo)) {
                getPetList(req, resp);
            } else if ("/detail".equals(pathInfo)) {
                getPetDetail(req, resp);
            } else if ("/all".equals(pathInfo)) {
                getAllPets(req, resp);
            } else {
                resp.getWriter().write(mapper.writeValueAsString(Result.error(404, "接口不存在")));
            }
        } catch (Exception e) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(e.getMessage())));
        }
    }

    private void getPetList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        List<?> pets = petService.getPetsByUserId(userId);
        resp.getWriter().write(mapper.writeValueAsString(Result.success(pets)));
    }

    private void getPetDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        Pet pet = petService.getPetById(Integer.parseInt(idStr));
        if (pet == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("宠物不存在")));
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(Result.success(pet)));
    }

    private void getAllPets(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<?> pets = petService.getAllPets();
        resp.getWriter().write(mapper.writeValueAsString(Result.success(pets)));
    }

    private void addPet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.getWriter().write(mapper.writeValueAsString(Result.error(401, "请先登录")));
            return;
        }

        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(req.getParameter("name"));
        pet.setSpecies(req.getParameter("species"));
        pet.setBreed(req.getParameter("breed"));
        String ageStr = req.getParameter("age");
        if (ageStr != null && !ageStr.isEmpty()) {
            pet.setAge(Integer.parseInt(ageStr));
        }
        pet.setGender(req.getParameter("gender"));
        String weightStr = req.getParameter("weight");
        if (weightStr != null && !weightStr.isEmpty()) {
            pet.setWeight(new BigDecimal(weightStr));
        }
        pet.setImage(req.getParameter("image"));
        pet.setHealthStatus(req.getParameter("healthStatus"));
        pet.setRemark(req.getParameter("remark"));

        if (petService.addPet(pet)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("添加成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("添加失败")));
        }
    }

    private void updatePet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pet pet = new Pet();
        pet.setId(Integer.parseInt(req.getParameter("id")));
        pet.setName(req.getParameter("name"));
        pet.setSpecies(req.getParameter("species"));
        pet.setBreed(req.getParameter("breed"));
        String ageStr = req.getParameter("age");
        if (ageStr != null && !ageStr.isEmpty()) {
            pet.setAge(Integer.parseInt(ageStr));
        }
        pet.setGender(req.getParameter("gender"));
        String weightStr = req.getParameter("weight");
        if (weightStr != null && !weightStr.isEmpty()) {
            pet.setWeight(new BigDecimal(weightStr));
        }
        pet.setImage(req.getParameter("image"));
        pet.setHealthStatus(req.getParameter("healthStatus"));
        pet.setRemark(req.getParameter("remark"));

        if (petService.updatePet(pet)) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("更新成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("更新失败")));
        }
    }

    private void deletePet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (petService.deletePet(Integer.parseInt(idStr))) {
            resp.getWriter().write(mapper.writeValueAsString(Result.success("删除成功")));
        } else {
            resp.getWriter().write(mapper.writeValueAsString(Result.error("删除失败")));
        }
    }
}
