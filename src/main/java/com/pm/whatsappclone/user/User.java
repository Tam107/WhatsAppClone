package com.pm.whatsappclone.user;

import com.pm.whatsappclone.chat.Chat;
import com.pm.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL,
        query = "SELECT u from User u where u.email = :email")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
        query = "SELECT u from User u where u.id != :publicId")
@NamedQuery(name = UserConstants.FIND_USER_BY_PUBLIC_ID,
        query = "SELECT u from User u where u.id = :publicId")
public class User extends BaseAuditingEntity {

    private static final long LAST_ACTIVE_INTERVAL = 5; // minutes

    @Id
    private String id; // id from Keycloak

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime lastSeen; // last seen time is online or offline

    private String password;

    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;

    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsRecipient;

    @Transient
    public boolean isUserOnline() {

        // lastseen = 10:05
        // now = 10:10
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().plusMinutes(LAST_ACTIVE_INTERVAL));
    }
}
