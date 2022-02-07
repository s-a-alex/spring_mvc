package web;

import dao.SingerService;
import entity.Singer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import util.Message;
import util.SingerGrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(value = "/singers")
public class SingerController {
    private final Logger logger = LoggerFactory.getLogger(SingerController.class);

    @Autowired
    private SingerService singerService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String singers(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                           @RequestParam(value = "rows", required = false, defaultValue = "5") Integer rows,
                           @RequestParam(value = "sidx", required = false) String sortBy,
                           @RequestParam(value = "sord", required = false) String orderBy, Model model) {
        Sort sort = (sortBy != null && orderBy != null ? (orderBy.equals("desc") ? new Sort(Sort.Direction.DESC, sortBy) : new Sort(Sort.Direction.ASC, sortBy)) : null);
        PageRequest pageRequest = (sort != null ? PageRequest.of(page - 1, rows, sort) : PageRequest.of(page - 1, rows));
        Page<Singer> singerPage = singerService.findAll(pageRequest);
        model.addAttribute("singerGrid", new SingerGrid(singerPage.getTotalPages(), singerPage.getNumber() + 1, singerPage.getTotalElements(),
                StreamSupport.stream(singerPage.stream().spliterator(), false).collect(Collectors.toList())));
        return "singers";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("singer", singerService.findById(id));
        return "show";
    }

    @GetMapping(value = "/edit/{id}")
    public String update(@PathVariable("id") Long id, Model model) {
        model.addAttribute("singer", singerService.findById(id));
        return "update";
    }

    @GetMapping(value = "/new")
    public String create(Model model) {
        model.addAttribute("singer", new Singer());
        return "update";
    }

    @PostMapping
    public String save(@Valid Singer singer, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Locale locale,
                       @RequestParam(value="file", required=false) Part file) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("singer", singer);
            return "update";
        }
        model.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("singer_save_success", new Object[]{}, locale)));
        try {
            if (file != null) singer.setPhoto(IOUtils.toByteArray(file.getInputStream()));
        } catch (IOException ex) {
            logger.error("Error saving uploaded file");
        }
        singerService.save(singer);
        return "redirect:/singers/" + singer.getId();
    }

    @GetMapping(value = "/photo/{id}")
    @ResponseBody
    public byte[] downloadPhoto(@PathVariable("id") Long id) {
        return singerService.findById(id).getPhoto();
    }
}
