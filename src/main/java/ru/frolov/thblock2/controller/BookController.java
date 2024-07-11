package ru.frolov.thblock2.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.frolov.thblock2.model.BookModel;
import ru.frolov.thblock2.service.BookService;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // в адресной строке браузера: http://localhost:8080/books?login=abc&email=abc%40mail.ru
    // params: "login" и "email"
    @GetMapping
    public String getBookPage(@RequestParam(required = false, name = "login") String login,
                              @RequestParam(required = false) String email,
                              Model model,
                              HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (login != null && !login.isEmpty()) {
            session.setAttribute("userLogin", login);
        }

        String userLogin = (String) session.getAttribute("userLogin");
        model.addAttribute("userLogin", userLogin);

        List<BookModel> books = this.bookService.getAllBooksByLogin(userLogin);
        model.addAttribute("userBooks", books);

        return "book_page";
    }

    @GetMapping("create")
    public String getCreateBookPage(Model model) {
        model.addAttribute("newBook", new BookModel());
        return "create_book_page";
    }

    @PostMapping("createBook")
    public String createBook(@ModelAttribute BookModel book) {
        this.bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("edit/{title}")
    public String getEditBookPage(Model model, @PathVariable String title) {
        BookModel byTitle = this.bookService.findByTitleAndDelete(title);
        model.addAttribute("bookToEdit", byTitle);
        return "edit_book_page";
    }

    @PostMapping("editBook")
    public String editBook(@ModelAttribute BookModel book) {
        this.bookService.edit(book);
        return "redirect:/books";
    }

    @GetMapping("delete/{title}")
    public String deleteBook(@PathVariable String title){
        this.bookService.findByTitleAndDelete(title);
        return "redirect:/books";
    }
}
