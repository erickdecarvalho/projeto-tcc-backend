package br.com.apimarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "reset_token")
public class ResetToken {

    private static final int expiration = 60*30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(nullable = false,name = "user_id")
    private User user;

    private Date expiryDate;

    public ResetToken(){
        this.expiryDate = expirationTime(expiration);
    }

    public ResetToken(User user,String token){
        this.user = user;
        this.token = token;
        this.expiryDate = expirationTime(expiration);
    }

    private Date expirationTime(final int expirationTimeMinutes){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expirationTimeMinutes);
        return new Date(calendar.getTime().getTime());
    }


}
