package com.alisimsek.LibraryManagementProject.service;

import com.alisimsek.LibraryManagementProject.dto.request.CategoryUpdateRequest;
import com.alisimsek.LibraryManagementProject.entity.Book;
import com.alisimsek.LibraryManagementProject.entity.Category;
import com.alisimsek.LibraryManagementProject.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookService bookService;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + " id'li kategori bulunamadı"));
    }

    public Category create(Category request) {
        categoryRepository.findByName(request.getName())
                .ifPresent(c -> {
                    throw new RuntimeException("Bu kategori daha önce sisteme kayıt olmuştur");
                });

        return categoryRepository.save(request);
    }

    public Category update(Long id, CategoryUpdateRequest request) {

        Category categoryFromDb = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + " id'li kategori bulunamadı"));

        categoryRepository.findByName(request.getName())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new RuntimeException("Bu kategori daha önce sisteme kayıt olmuştur");
                });

        // 🔥 SADECE GÜNCELLENMESİ GEREKEN ALANLAR
        categoryFromDb.setName(request.getName());
        categoryFromDb.setDescription(request.getDescription());

        // ❗ books alanına dokunmuyoruz
        return categoryRepository.save(categoryFromDb);
    }

    public String deleteById(Long id) {

        Category categoryFromDb = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + " id'li kategori sistemde bulunamadı"));

        List<Book> booksInCategory = bookService.findByCategoryId(id);

        if (!booksInCategory.isEmpty()) {
            return "Bu kategoriye ait kayıtlı kitaplar mevcut. Silme işlemi yapılamadı.";
        }

        categoryRepository.delete(categoryFromDb);
        return "Kategori silme işlemi başarılı";
    }
}
