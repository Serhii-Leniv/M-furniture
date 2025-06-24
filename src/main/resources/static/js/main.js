
function searchProducts() {
    const searchTerm = document.getElementById('searchInput').value.trim();
    const searchResultsList = document.getElementById('searchResultsList');

    if (searchTerm === '') {
        hideSearchResults();
        return;
    }

    fetch(`/api/products/search?query=${encodeURIComponent(searchTerm)}`)
        .then(response => {
            if (!response.ok) throw new Error('Помилка пошуку');
            return response.json();
        })
        .then(products => {
            searchResultsList.innerHTML = '';

            if (products.length === 0) {
                searchResultsList.innerHTML = '<div class="no-results">Нічого не знайдено</div>';
                showSearchResults();
                return;
            }

            products.forEach(product => {
                const resultItem = document.createElement('a');
                resultItem.href = `/product/${product.id}`;
                resultItem.className = 'list-group-item list-group-item-action search-result-item';

                const highlightedName = highlightSearchTerm(
                    product.name.toLowerCase(),
                    searchTerm.toLowerCase()
                );

                resultItem.innerHTML = `
                    <img src="${product.imageUrl || '/no-image.png'}" 
                         onerror="this.src='/no-image.png'">
                    <div class="search-result-info">
                        <div>${highlightedName}</div>
                        <small class="text-muted">${product.price} грн</small>
                    </div>
                `;

                searchResultsList.appendChild(resultItem);
            });

            showSearchResults();
        })
        .catch(error => {
            console.error('Помилка:', error);
            searchResultsList.innerHTML = '<div class="no-results">Помилка пошуку</div>';
            showSearchResults();
        });
}
function highlightSearchTerm(text, term) {
    const regex = new RegExp(term, 'gi');
    return text.replace(regex, match => `<span class="search-highlight">${match}</span>`);
}

document.getElementById('searchInput').addEventListener('input', function (e) {
    const clearIcon = document.getElementById('clearSearch');
    clearIcon.style.display = e.target.value ? 'block' : 'none';
    searchProducts();
});

document.getElementById('clearSearch').addEventListener('click', function () {
    document.getElementById('searchInput').value = '';
    this.style.display = 'none';
    hideSearchResults();
});

function showSearchResults() {
    const dropdown = document.getElementById('searchResultsDropdown');
    dropdown.classList.add('show');
    dropdown.style.display = 'block';
}

function hideSearchResults() {
    const dropdown = document.getElementById('searchResultsDropdown');
    dropdown.classList.remove('show');
    dropdown.style.display = 'none';
}

function getCsrfToken() {
    return document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='))
        ?.split('=')[1];
}

function addToCart(productId) {
    fetch(`/api/cart/add?productId=${productId}`, {
        method: 'POST',
        headers: {
            'X-XSRF-TOKEN': getCsrfToken()
        },
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            showCartNotification(data.message);
            updateCartDropdown();
        })
        .catch(error => console.error('Помилка:', error));
}

function removeFromCart(productId) {
    fetch(`/api/cart/remove?productId=${productId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) throw new Error(response.statusText);
            return response.json();
        })
        .then(data => {
            showCartNotification(data.message);
            updateCartDropdown();
        })
        .catch(error => {
            console.error('Помилка:', error);
            alert('Помилка при видаленні товару з кошика');
        });
}

function increaseQuantity(productId) {
    fetch(`/api/cart/increase?productId=${productId}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) throw new Error(response.statusText);
            return response.json();
        })
        .then(data => {
            updateCartDropdown();
        })
        .catch(error => {
            console.error('Помилка:', error);
            alert('Помилка при збільшенні кількості');
        });
}

function decreaseQuantity(productId) {
    fetch(`/api/cart/decrease?productId=${productId}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) throw new Error(response.statusText);
            return response.json();
        })
        .then(data => {
            updateCartDropdown();
        })
        .catch(error => {
            console.error('Помилка:', error);
            alert('Помилка при зменшенні кількості');
        });
}
function updateCartDropdown() {
    fetch('/api/cart/items')
        .then(response => {
            if (!response.ok) throw new Error('Помилка завантаження кошика');
            return response.json();
        })
        .then(data => {
            const cartItemsContainer = document.getElementById('cartItemsContainer');
            const cartCount = document.getElementById('cartCount');
            const cartTotal = document.getElementById('cartTotal');
            const checkoutBtn = document.getElementById('checkoutButton');

            // Оновлення загальної кількості товарів
            const totalItems = data.items?.length || 0;
            cartCount.textContent = totalItems;

            // Перемикач для кнопки оформлення
            checkoutBtn.style.display = totalItems > 0 ? 'block' : 'none';

            // Оновлення списку товарів
            if (totalItems > 0) {
                const groupedItems = data.items.reduce((acc, item) => {
                    if (!acc[item.id]) {
                        acc[item.id] = {
                            ...item,
                            quantity: 0,
                            total: 0,
                            imageUrl: item.imageUrl || '/no-image.png'
                        };
                    }
                    acc[item.id].quantity++;
                    acc[item.id].total += item.price;
                    return acc;
                }, {});

                cartItemsContainer.innerHTML = `
                    <div class="cart-items-list">
                        ${Object.values(groupedItems).map(item => `
                            <div class="cart-item" data-id="${item.id}">
                                <img src="${item.imageUrl}"
                                     alt="${item.name}"
                                     class="cart-item-img"
                                     onerror="this.src='/no-image.png'">
                                <div class="cart-item-info">
                                    <div class="cart-item-name">${item.name}</div>
                                    <div class="cart-item-price">${item.price.toFixed(2)} грн/шт</div>
                                    <div class="cart-item-controls">
                                        <button class="quantity-btn"
                                                onclick="decreaseQuantity(${item.id})">−</button>
                                        <span class="quantity-value">${item.quantity}</span>
                                        <button class="quantity-btn"
                                                onclick="increaseQuantity(${item.id})">+</button>
                                    </div>
                                </div>
                                <div class="cart-item-actions">
                                    <div class="cart-item-total">${item.total.toFixed(2)} грн</div>
                                    <button class="remove-item"
                                            onclick="removeFromCart(${item.id})">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                `;

                // Оновлення загальної суми
                const totalPrice = data.items.reduce((sum, item) => sum + item.price, 0);
                cartTotal.textContent = `${totalPrice.toFixed(2)} грн`;
            } else {
                cartItemsContainer.innerHTML = `
                    <div class="empty-cart text-center py-4">
                        <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                        <p class="text-muted">Кошик порожній</p>
                    </div>
                `;
                cartTotal.textContent = '0.00 грн';
            }
        })
        .catch(error => {
            console.error('Помилка:', error);
            document.getElementById('checkoutButton').style.display = 'none';
            cartItemsContainer.innerHTML = `
                <div class="error-message text-danger p-3">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Не вдалось завантажити вміст кошика
                </div>
            `;
            addCartEventListeners();
        });
}

const checkoutForm = document.querySelector('#checkoutButton').closest('form');
if (checkoutForm) {
    checkoutForm.addEventListener('click', function(e) {
        e.stopPropagation();
    });
}

function showCartNotification(message) {
    const notification = document.getElementById('cart-notification');
    notification.innerHTML = `<i class="fas fa-shopping-cart"></i> ${message}`;
    notification.classList.add('show');
    setTimeout(() => notification.classList.remove('show'), 2000);
}

document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');

    let searchTimeout;

    searchInput.addEventListener('input', function () {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(searchProducts, 300);
    });


    searchInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            searchProducts();
        }
    });

    document.addEventListener('click', function (e) {
        if (!e.target.closest('.search-container') && !e.target.closest('#searchResultsDropdown')) {
            hideSearchResults();
        }
    });

    document.querySelectorAll('img').forEach(img => {
        img.onerror = function () {
            if (this.src.includes('no-image.png')) return;
            this.src = '/no-image.png';
        };
    });

function addCartEventListeners() {
    document.querySelectorAll('.quantity-btn, .remove-item').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation(); // Зупиняємо спливання

            // Викликаємо відповідну функцію
            const action = this.dataset.action;
            const productId = this.dataset.id;

            if (action === 'increase') increaseQuantity(productId);
            if (action === 'decrease') decreaseQuantity(productId);
            if (action === 'remove') removeFromCart(productId);
        });
    });
}

    document.addEventListener('DOMContentLoaded', function() {
        const cartDropdown = document.getElementById('cartDropdown');

        if (cartDropdown) {
            cartDropdown.addEventListener('click', function(e) {
                e.stopPropagation(); // Зупиняємо спливання події
            });
        }
    });

    updateCartDropdown();
    document.addEventListener('DOMContentLoaded', function () {
        updateCartDropdown(); // Оновлюємо кошик при завантаженні сторінки
    });

});