package jp.co.seattle.library.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.seattle.library.service.UsersService;

public class LoginCheckController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UsersService usersService;

}
