package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", employeeService.findAll().size());
        model.addAttribute("employeeList", employeeService.findAll());

        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);
        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

    // 従業員更新画面
    @GetMapping(value = "/{code}/update")
    public String edit(@PathVariable String code, Model model) {
        model.addAttribute("employee", employeeService.findByCode(code));
        return "employees/update";
    }

    // 従業員更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable String code, @Validated Employee employee, BindingResult res, Model model) {

        // 氏名のチェック
        if (employee.getName() == null || employee.getName().isEmpty()) {
            // 入力が空の場合、エラーメッセージを表示
            model.addAttribute("nameError", "値を入力してください");
            return "employees/update";

        } else if (employee.getName().length() > 20) {
            // 桁数が20文字を超える場合、エラーメッセージを設定
            model.addAttribute("nameError", "20文字以下で入力してください");
            return "employees/update";
        }

        // パスワードが空の場合
        if ("".equals(employee.getPassword())) {
            // パスワードの更新を行わず、その他の従業員情報のみを更新
            ErrorKinds result = employeeService.updateEmployeeInfoWithoutPassword(employee);
            if (result != ErrorKinds.SUCCESS) {
                // 更新が失敗した場合、更新画面でエラーメッセージを表示
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return "employees/update";
            }
        } else {
            // パスワードが空でない場合、従業員情報とパスワードの更新を行う
            ErrorKinds result = employeeService.save(employee);
            if (result != ErrorKinds.SUCCESS) {
                // 更新が失敗した場合、更新画面でエラーメッセージを表示
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return "employees/update";
            }
        }
        // 入力チェック
        if (res.hasErrors()) {
            return "employees/update";
        }

        // 更新が成功した場合は従業員一覧画面にリダイレクト
        return "redirect:/employees";
    }

}
