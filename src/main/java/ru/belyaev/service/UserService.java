/*
 * Created by Vologda Developer
 * Date: 20.06.2020
 * Time: 13:53
 */

package ru.belyaev.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.belyaev.entity.User;

public interface UserService extends UserDetailsService {

    void addUser(User user);

}