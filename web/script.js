// OpenOtkPlatform 网页功能脚本

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 用户管理
let currentUser = null;

// API请求工具函数
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API请求失败:', error);
        throw error;
    }
}

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    loadProducts();
    checkLoginStatus();
});

// 加载商品列表
async function loadProducts() {
    const productsGrid = document.getElementById('productsGrid');
    if (!productsGrid) return;

    productsGrid.innerHTML = '<p style="text-align: center; color: #586069;">加载中...</p>';
    
    try {
        const products = await apiRequest('/items');
        productsGrid.innerHTML = '';
        
        if (products.length === 0) {
            productsGrid.innerHTML = '<p style="text-align: center; color: #586069; grid-column: 1 / -1;">暂无商品</p>';
            return;
        }
        
        products.forEach(product => {
            const productCard = createProductCard(product);
            productsGrid.appendChild(productCard);
        });
    } catch (error) {
        productsGrid.innerHTML = '<p style="text-align: center; color: #ff6b6b; grid-column: 1 / -1;">加载商品失败</p>';
        console.error('加载商品失败:', error);
    }
}

// 创建商品卡片
function createProductCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.onclick = () => viewProductDetail(product.id);
    
    card.innerHTML = `
        <div class="product-image">
            <img src="${product.image}" alt="${product.name}">
        </div>
        <h3 class="product-name">${product.name}</h3>
        <p class="product-description">${product.description}</p>
        <div class="product-price">¥${product.price.toFixed(2)}</div>
        <div class="product-seller">卖家: ${product.seller}</div>
        <button class="btn btn-primary" onclick="event.stopPropagation(); viewProductDetail(${product.id})">查看详情</button>
    `;
    
    return card;
}

// 搜索商品
async function searchProducts() {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput.value.trim();
    
    if (!keyword) {
        loadProducts();
        return;
    }
    
    const productsGrid = document.getElementById('productsGrid');
    if (!productsGrid) return;
    
    productsGrid.innerHTML = '<p style="text-align: center; color: #586069;">搜索中...</p>';
    
    try {
        const products = await apiRequest(`/items/search?keyword=${encodeURIComponent(keyword)}`);
        productsGrid.innerHTML = '';
        
        if (products.length === 0) {
            productsGrid.innerHTML = '<p style="text-align: center; color: #586069; grid-column: 1 / -1;">没有找到相关商品</p>';
            return;
        }
        
        products.forEach(product => {
            const productCard = createProductCard(product);
            productsGrid.appendChild(productCard);
        });
    } catch (error) {
        productsGrid.innerHTML = '<p style="text-align: center; color: #ff6b6b; grid-column: 1 / -1;">搜索失败</p>';
        console.error('搜索商品失败:', error);
    }
}

// 查看商品详情
function viewProductDetail(productId) {
    // 存储当前查看的商品ID到localStorage
    localStorage.setItem('currentProductId', productId);
    window.location.href = 'product.html';
}

// 加载商品详情
async function loadProductDetail() {
    const productId = parseInt(localStorage.getItem('currentProductId'));
    
    if (!productId) {
        alert('商品ID无效');
        window.location.href = 'index.html';
        return;
    }
    
    try {
        const product = await apiRequest(`/items/${productId}`);
        
        // 更新页面内容
        document.getElementById('productName').textContent = product.name;
        document.getElementById('productDescription').textContent = product.description;
        document.getElementById('productPrice').textContent = `¥${product.price.toFixed(2)}`;
        document.getElementById('productStock').textContent = `库存: ${product.stock}`;
        document.getElementById('productSeller').textContent = `卖家ID: ${product.sellerId}`;
        document.getElementById('productImage').src = `https://via.placeholder.com/400x300/24292e/ffffff?text=${encodeURIComponent(product.name)}`;
    } catch (error) {
        alert('商品不存在或加载失败');
        window.location.href = 'index.html';
        console.error('加载商品详情失败:', error);
    }
}

// 购买商品
async function buyProduct() {
    if (!currentUser) {
        alert('请先登录后再购买');
        window.location.href = 'user.html';
        return;
    }
    
    const productId = parseInt(localStorage.getItem('currentProductId'));
    
    if (!productId) {
        alert('商品ID无效');
        return;
    }
    
    try {
        const product = await apiRequest(`/items/${productId}`);
        
        if (product.stock <= 0) {
            alert('商品已售罄');
            return;
        }
        
        const confirmBuy = confirm(`确定要购买 ${product.name} 吗？\n价格: ¥${product.price.toFixed(2)}`);
        
        if (confirmBuy) {
            // 创建订单
            const orderData = {
                itemId: productId,
                buyerId: currentUser.id,
                sellerId: product.sellerId,
                totalPrice: product.price
            };
            
            await apiRequest('/orders', {
                method: 'POST',
                body: JSON.stringify(orderData)
            });
            
            alert('购买成功！请联系卖家完成交易。');
            window.location.href = 'index.html';
        }
    } catch (error) {
        alert('购买失败，请稍后重试');
        console.error('购买商品失败:', error);
    }
}

// 用户认证功能
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    if (!username || !password) {
        alert('请输入用户名和密码');
        return;
    }
    
    try {
        const response = await apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
        if (response.success) {
            currentUser = response.user;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            checkLoginStatus();
            alert('登录成功！');
            window.location.href = 'index.html';
        } else {
            alert(response.message || '登录失败');
        }
    } catch (error) {
        alert('登录失败，请检查网络连接');
        console.error('登录失败:', error);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const email = document.getElementById('regEmail').value;
    const phone = document.getElementById('regPhone').value;
    
    if (!username || !password || !email || !phone) {
        alert('请填写所有必填字段');
        return;
    }
    
    try {
        const response = await apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, password, email, phone })
        });
        
        if (response.success) {
            alert('注册成功！请登录');
            showLogin();
        } else {
            alert(response.message || '注册失败');
        }
    } catch (error) {
        alert('注册失败，请稍后重试');
        console.error('注册失败:', error);
    }
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
}

function showLogin() {
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('loginForm').style.display = 'block';
}

function logout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    checkLoginStatus();
    alert('已退出登录');
    window.location.href = 'index.html';
}

// 检查登录状态
function checkLoginStatus() {
    const storedUser = localStorage.getItem('currentUser');
    
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
    }
    
    // 更新导航栏用户信息
    const userInfoElements = document.querySelectorAll('#userInfo');
    const loginLinks = document.querySelectorAll('.nav-link[href="user.html"]');
    const userProfile = document.getElementById('userProfile');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    
    if (currentUser) {
        // 更新导航栏
        userInfoElements.forEach(element => {
            element.textContent = `欢迎, ${currentUser.username}`;
            element.style.display = 'inline';
        });
        
        loginLinks.forEach(link => {
            link.textContent = '用户中心';
        });
        
        // 更新用户中心页面
        if (userProfile && loginForm && registerForm) {
            loginForm.style.display = 'none';
            registerForm.style.display = 'none';
            userProfile.style.display = 'block';
            
            document.getElementById('profileUsername').textContent = currentUser.username;
            document.getElementById('profileEmail').textContent = currentUser.email;
            document.getElementById('profilePhone').textContent = currentUser.phone;
            document.getElementById('profileBalance').textContent = currentUser.balance.toFixed(2);
        }
    } else {
        // 用户未登录
        userInfoElements.forEach(element => {
            element.style.display = 'none';
        });
        
        loginLinks.forEach(link => {
            link.textContent = '登录/注册';
        });
        
        // 更新用户中心页面
        if (userProfile && loginForm && registerForm) {
            userProfile.style.display = 'none';
            loginForm.style.display = 'block';
            registerForm.style.display = 'none';
        }
    }
}

// 全局搜索功能（支持回车键）
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                searchProducts();
            }
        });
    }
});
