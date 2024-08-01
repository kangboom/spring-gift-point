package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gift.domain.category.entity.Category;
import gift.domain.member.entity.Member;
import gift.domain.member.repository.MemberRepository;
import gift.domain.product.entity.Product;
import gift.domain.product.exception.ProductNotFoundException;
import gift.domain.product.repository.ProductRepository;
import gift.domain.wishlist.dto.WishRequest;
import gift.domain.wishlist.dto.WishResponse;
import gift.domain.wishlist.entity.Wish;
import gift.domain.wishlist.repository.WishRepository;
import gift.domain.wishlist.service.WishService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {

    @InjectMocks
    WishService wishService;
    @Mock
    WishRepository wishRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ProductRepository productRepository;

    @Test
    @DisplayName("getWishesByMember 테스트")
    void getWishesByMemberTest() {
        // given
        Member savedMember = createMember();

        Product product1 = createProduct();
        Product product2 = createProduct(2L,
            new Category(2L, "test", "color", "image", "description"));

        Wish wish1 = new Wish(savedMember, product1, LocalDateTime.now());
        Wish wish2 = new Wish(savedMember, product2, LocalDateTime.now());
        List<Wish> wishList = Arrays.asList(wish1, wish2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishPage = new PageImpl<>(wishList, pageable, wishList.size());

        doReturn(wishPage).when(wishRepository).findAllByMember(savedMember, pageable);

        Page<WishResponse> expected = wishPage.map(this::entityToDto);

        // when
        Page<WishResponse> actual = wishService.getWishesByMember(savedMember, pageable);

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> IntStream.range(0, actual.getContent().size()).forEach(i -> {
                assertThat(actual.getContent().get(i).id())
                    .isEqualTo(expected.getContent().get(i).id());
                assertThat(actual.getContent().get(i).name())
                    .isEqualTo(expected.getContent().get(i).name());
                assertThat(actual.getContent().get(i).price())
                    .isEqualTo(expected.getContent().get(i).price());
                assertThat(actual.getContent().get(i).imageUrl())
                    .isEqualTo(expected.getContent().get(i).imageUrl());
            })
        );
    }

    @Test
    @DisplayName("위시 리스트 추가 테스트")
    void createWishTest() {
        // given
        WishRequest wishRequest = new WishRequest(1L, 1L);

        Member savedMember = createMember();
        Product savedProduct = createProduct();

        doReturn(Optional.of(savedMember)).when(memberRepository)
            .findById(wishRequest.getMemberId());
        doReturn(Optional.of(savedProduct)).when(productRepository)
            .findById(wishRequest.getProductId());

        // when
        // then
        assertDoesNotThrow(()-> wishService.createWish(wishRequest));
    }

    @Test
    @DisplayName("위시 리시트 삭제 테스트")
    void deleteWishTest() {
        Long id = 1L;
        Member savedMember = createMember();
        Product savedProduct = createProduct();

        Wish wish = new Wish(savedMember, savedProduct, LocalDateTime.now());

        doReturn(Optional.of(savedProduct)).when(productRepository).findById(savedProduct.getId());
        doReturn(Optional.of(wish)).when(wishRepository).findByProductAndMember(savedProduct,savedMember);

        assertDoesNotThrow(()->wishService.deleteWish(id, savedMember));
        verify(wishRepository, times(1)).delete(any(Wish.class));
    }

    private WishResponse entityToDto(Wish wish) {
        Product productInWish = wish.getProduct();
        return new WishResponse(productInWish.getId(), productInWish.getName(),
            productInWish.getPrice(), productInWish.getImageUrl());
    }

    Member createMember() {
        return new Member(1L, "email@google.co.kr", "password");
    }

    private Product createProduct() {
        return createProduct(1L, new Category(1L, "test", "color", "image", "description"));
    }

    private Product createProduct(Long id, Category category) {
        return new Product(id, "test", 1000, "test.jpg", category);
    }
}