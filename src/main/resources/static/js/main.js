function selectCategory(category) {
    document.getElementById('carouselExample').classList.add('hidden');
    document.getElementById('categoriesContainer').classList.remove('hidden');
    document.querySelectorAll('.category-section').forEach(section => {
        section.classList.add('hidden');
    });
    const selectedCategory = document.getElementById(category);
    if (selectedCategory) {
        selectedCategory.classList.remove('hidden');
        selectedCategory.scrollIntoView({behavior: 'smooth'});
    }
    const categoryBoxes = document.querySelectorAll('.category-box');
    categoryBoxes.forEach(box => {
        box.classList.remove('active');
        if (box.getAttribute('data-category') === category) {
            box.classList.add('active');
        }
    });
}

function searchProducts() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase().trim();
    const searchResultsList = document.getElementById('searchResultsList');

    if (searchTerm === '') {
        hideSearchResults();
        return;
    }

    const productItems = document.querySelectorAll('.product-item');
    let foundResults = false;

    searchResultsList.innerHTML = '';

    productItems.forEach(item => {
        const productName = item.querySelector('.product-name').textContent.toLowerCase();
        const productLink = item.querySelector('a').getAttribute('href');
        const productImage = item.querySelector('img').getAttribute('src');
        const productPrice = item.querySelector('p').textContent;

        if (productName.includes(searchTerm)) {
            foundResults = true;
            const resultItem = document.createElement('a');
            resultItem.href = productLink;
            resultItem.className = 'list-group-item list-group-item-action search-result-item';

            const highlightedName = highlightSearchTerm(productName, searchTerm);

            resultItem.innerHTML = `
                    <img src="${productImage}" onerror="this.src='/no-image.png'">
                    <div class="search-result-info">
                        <div>${highlightedName}</div>
                        <small class="text-muted">${productPrice}</small>
                    </div>
                `;

            searchResultsList.appendChild(resultItem);
        }
    });

    if (!foundResults) {
        searchResultsList.innerHTML = '<div class="no-results">Нічого не знайдено</div>';
    }

    showSearchResults();
}

function highlightSearchTerm(text, term) {
    const regex = new RegExp(term, 'gi');
    return text.replace(regex, match => `<span class="search-highlight">${match}</span>`);
}

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
            if (!response.ok) throw new Error(response.statusText);
            return response.json();
        })
        .then(data => {
            const cartItemsContainer = document.getElementById('cartItemsContainer');
            const cartCount = document.getElementById('cartCount');
            const cartTotal = document.getElementById('cartTotal');

            if (data.items && data.items.length > 0) {
                const groupedItems = data.items.reduce((acc, item) => {
                    if (!acc[item.id]) {
                        acc[item.id] = {
                            ...item,
                            quantity: 0,
                            imageUrl: item.imageUrl || '/no-image.png'
                        };
                    }
                    acc[item.id].quantity++;
                    return acc;
                }, {});

                cartItemsContainer.innerHTML = `
                        <h6 class="mb-3">Товари у кошику</h6>
                        ${Object.values(groupedItems).map(item => `
                            <div class="cart-item">
                                <img src="${item.imageUrl}"
                                     onerror="this.src='/no-image.png'"
                                     class="cart-item-img">
                                <div class="cart-item-info">
                                    <div class="cart-item-name">${item.name}</div>
                                    <div class="cart-item-price">${item.price} грн/шт</div>
                                    <div class="cart-item-controls">
                                        <button class="quantity-btn"
                                                onclick="decreaseQuantity(${item.id})">-</button>
                                        <span class="quantity-value">${item.quantity}</span>
                                        <button class="quantity-btn"
                                                onclick="increaseQuantity(${item.id})">+</button>
                                    </div>
                                </div>
                                <div class="text-end">
                                    <div class="fw-bold">${(item.price * item.quantity).toFixed(2)} грн</div>
                                    <div class="remove-item"
                                         onclick="removeFromCart(${item.id})">
                                        Видалити
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    `;
                cartCount.textContent = data.items.length;
                cartCount.style.display = 'block';
                cartTotal.textContent = data.total.toFixed(2) + ' грн';
            } else {
                cartItemsContainer.innerHTML = `
                        <div class="empty-cart">
                            <i class="fas fa-shopping-cart fa-2x mb-2"></i>
                            <p>Кошик порожній</p>
                        </div>
                    `;
                cartCount.style.display = 'none';
                cartTotal.textContent = '0 грн';
            }
        })
        .catch(error => {
            console.error('Помилка:', error);
            alert('Помилка при оновленні кошика');
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

    searchButton.addEventListener('click', searchProducts);

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

    updateCartDropdown();
});