package gift.domain.category.controller;

import gift.domain.category.dto.CategoriesResponse;
import gift.domain.category.dto.CategoryRequest;
import gift.domain.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "CategoryController", description = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @Operation(summary = "카테고리 전체 조회", description = "카테고리 전체를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode="200", description = "요청에 성공하였습니다.", content =  @Content(schema = @Schema(implementation = CategoriesResponse.class), mediaType = "application/json")),
        @ApiResponse(responseCode="500", description = "서버 오류", content = @Content(mediaType = "application/json")),
    })
    @GetMapping()
    public ResponseEntity<CategoriesResponse> getAllCategories() {
        CategoriesResponse categories = new CategoriesResponse(categoryService.getAllCategories());
        return ResponseEntity.ok(categories);
    }


    @Operation(summary = "카테고리 생성", description = "카테고리를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode="201", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode="500", description = "서버 오류", content = @Content(mediaType = "application/json")),
    })
    @PostMapping()
    public ResponseEntity<Void> createCategory(@RequestBody CategoryRequest request) {
        categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카테고리 수정", description = "해당 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameter(name = "id", description = "수정할 카테고리 id", example = "1")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(
        @PathVariable("id") Long id,
        @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카테고리 삭제", description = "해당 카테고리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameter(name = "id", description = "삭제할 카테고리 id", example = "1")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}