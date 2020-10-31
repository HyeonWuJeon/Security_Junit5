package com.foodPro.demo.ApiTest;


import com.foodPro.demo.config.common.Address;
import com.foodPro.demo.config.exception.MemberDuplicationException;
import com.foodPro.demo.config.security.Role;
import com.foodPro.demo.member.domain.Member;
import com.foodPro.demo.member.dto.MemberDto;
import com.foodPro.demo.member.repository.MemberRepository;
import com.foodPro.demo.member.service.MemberServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceImplTest {

    @Autowired
    private MemberServiceImpl memberServiceImpl;
    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    //given
    static String name = "";
    static String pwd = "12345";
    static String phone = "0109259";
    static String birth = "1995-11-29";
    static String email = "yusa3@naver.com";
    static String city = "서울";
    static String zipcode = "330";
    static String street = "용마산로";
    static Role role = Role.ADMIN;


    @Test
    public void 회원조회(){
//        Page<Member> List = memberService.findAllDesc(Pageable.unpaged());
//        System.out.println("List = " + List);
//
//        for (Member member : List){
//            System.out.println("member.toString() = " + member.toString());
//        }
    }

    @Test
    @Rollback(false)
    public void 회원가입(){
        //given
        Address address = new Address(city, zipcode, street);
        MemberDto.Request request = new MemberDto.Request();
        int age = Integer.parseInt(birth.substring(0,4));
        int year = Calendar.getInstance().get(Calendar.YEAR);
         request.builder()
                .name(name)
                .pwd(pwd)
                .pwdChk(pwd)
                 .low_pwd(pwd)
                .email(email)
                .birth(birth)
                .phone(phone)
                .zipcode(zipcode)
                .city(city)
                .street(street)
                .role(role)
                 .age(year - age)
                .build();
        //when
        memberServiceImpl.SignUp(request);
        //then
        Member member = memberRepository.findById(1L).get();
        assertThat(member.getName()).isEqualTo(name);
    }


    @Test(expected = MemberDuplicationException.class)
    public void 이메일_중복검사(){

        memberServiceImpl.validateDuplicateMember(email);

    }

    @Test
    @Rollback(false)
    public void 회원수정(){
        //given
        Address address = new Address("경기도", "광명", "00동");
        MemberDto.Request request = new MemberDto.Request().builder()
                .city(address.getCity())
                .street(address.getStreet())
                .zipcode(address.getZipcode())
                .phone("01012341234")
                .build();
        memberServiceImpl.update(1L, request);

        Member member = memberRepository.findById(1L).get();
        assertThat(member.getAddress().getCity()).isEqualTo("경기도");

    }

}