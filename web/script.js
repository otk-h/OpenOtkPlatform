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
        
        const data = await response.json();
        
        // 检查响应是否包含success字段
        if (data.success !== undefined) {
            if (!data.success) {
                throw new Error(data.message || '请求失败');
            }
            // 如果成功且有data字段，返回data；否则返回整个响应
            return data.data !== undefined ? data.data : data;
        }
        
        // 如果没有success字段，直接返回数据
        return data;
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
            <img src="https://via.placeholder.com/200x150/24292e/ffffff?text=${encodeURIComponent(product.name)}" alt="${product.name}">
        </div>
        <h3 class="product-name">${product.name}</h3>
        <p class="product-description">${product.description}</p>
        <div class="product-price">¥${product.price.toFixed(2)}</div>
        <div class="product-seller">卖家ID: ${product.sellerId}</div>
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
        
        // 设置数量输入框的最大值
        const quantityInput = document.getElementById('quantityInput');
        if (quantityInput) {
            quantityInput.max = product.stock;
            quantityInput.value = 1;
            updateTotalPricePreview(product.price, 1);
        }
        
        // 添加数量变化监听器
        if (quantityInput) {
            quantityInput.addEventListener('input', function() {
                const quantity = parseInt(this.value) || 1;
                updateTotalPricePreview(product.price, quantity);
            });
        }
    } catch (error) {
        alert('商品不存在或加载失败');
        window.location.href = 'index.html';
        console.error('加载商品详情失败:', error);
    }
}

// 更新总价预览
function updateTotalPricePreview(price, quantity) {
    const totalPricePreview = document.getElementById('totalPricePreview');
    if (totalPricePreview) {
        const totalPrice = price * quantity;
        totalPricePreview.textContent = `总价: ¥${totalPrice.toFixed(2)}`;
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
        
        // 获取购买数量
        const quantityInput = document.getElementById('quantityInput');
        let quantity = parseInt(quantityInput.value) || 1;
        
        // 验证数量
        if (quantity <= 0) {
            alert('购买数量必须大于0');
            return;
        }
        
        if (quantity > product.stock) {
            alert(`购买数量不能超过库存数量 (${product.stock})`);
            return;
        }
        
        // 计算总价
        const totalPrice = product.price * quantity;
        
        const confirmBuy = confirm(`确定要购买 ${product.name} 吗？\n数量: ${quantity}\n单价: ¥${product.price.toFixed(2)}\n总价: ¥${totalPrice.toFixed(2)}`);
        
        if (confirmBuy) {
            // 创建订单
            const orderData = {
                itemId: productId,
                buyerId: currentUser.id,
                sellerId: product.sellerId,
                quantity: quantity,
                totalPrice: totalPrice
            };
            
        await apiRequest('/orders', {
            method: 'POST',
            body: JSON.stringify(orderData)
        });
        
        // 刷新用户信息以显示更新后的余额
        await refreshUserInfo();
        
        alert(`购买成功！购买了 ${quantity} 件 ${product.name}，总价 ¥${totalPrice.toFixed(2)}。`);
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
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        // 根据后端AuthController的LoginResponse格式处理
        if (data.success) {
            currentUser = data.user;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            checkLoginStatus();
            alert('登录成功！');
            window.location.href = 'index.html';
        } else {
            alert(data.message || '登录失败');
        }
    } catch (error) {
        alert('登录失败: ' + error.message);
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
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, email, phone })
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('注册成功！请登录');
            showLogin();
        } else {
            alert(data.message || '注册失败');
        }
    } catch (error) {
        alert('注册失败: ' + error.message);
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

async function handleLogout() {
    if (currentUser) {
        try {
            // 调用后端logout接口记录系统日志
            await apiRequest(`/auth/logout?userId=${currentUser.id}`, {
                method: 'POST'
            });
        } catch (error) {
            console.error('记录退出日志失败:', error);
            // 即使记录失败也继续执行退出操作
        }
    }
    
    currentUser = null;
    localStorage.removeItem('currentUser');
    checkLoginStatus();
    alert('已退出登录');
    window.location.href = 'index.html';
}


// 余额充值功能
async function rechargeBalance() {
    if (!currentUser) {
        alert('请先登录');
        return;
    }
    
    const amountInput = document.getElementById('rechargeAmount');
    const amount = parseFloat(amountInput.value);
    
    if (!amount || amount <= 0) {
        alert('请输入有效的充值金额');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/${currentUser.id}/recharge?amount=${amount}`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('充值成功！');
            amountInput.value = '';
            // 刷新用户信息
            await refreshUserInfo();
        } else {
            alert(data.message || '充值失败');
        }
    } catch (error) {
        alert('充值失败: ' + error.message);
        console.error('充值失败:', error);
    }
}

// 刷新用户信息
async function refreshUserInfo() {
    if (!currentUser) return;
    
    try {
        const user = await apiRequest(`/users/${currentUser.id}`);
        if (user) {
            currentUser = user;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            checkLoginStatus();
        }
    } catch (error) {
        console.error('刷新用户信息失败:', error);
    }
}

// 商品发布功能
function showPublishForm() {
    document.getElementById('publishModal').style.display = 'flex';
}

function hidePublishForm() {
    document.getElementById('publishModal').style.display = 'none';
    // 清空表单
    document.getElementById('itemName').value = '';
    document.getElementById('itemDescription').value = '';
    document.getElementById('itemPrice').value = '';
    document.getElementById('itemStock').value = '';
}

async function handlePublishItem(event) {
    event.preventDefault();
    
    if (!currentUser) {
        alert('请先登录');
        return;
    }
    
    const name = document.getElementById('itemName').value.trim();
    const description = document.getElementById('itemDescription').value.trim();
    const price = parseFloat(document.getElementById('itemPrice').value);
    const stock = parseInt(document.getElementById('itemStock').value);
    
    if (!name || !description || !price || !stock) {
        alert('请填写所有必填字段');
        return;
    }
    
    if (price <= 0) {
        alert('价格必须大于0');
        return;
    }
    
    if (stock <= 0) {
        alert('库存数量必须大于0');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/items`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                description: description,
                price: price,
                sellerId: currentUser.id,
                stock: stock
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('商品发布成功！');
            hidePublishForm();
            // 刷新用户商品列表
            loadUserItems();
            await refreshUserInfo();
        } else {
            alert('商品发布失败: ' + (data.message || '未知错误'));
        }
    } catch (error) {
        alert('商品发布失败: ' + error.message);
        console.error('发布商品失败:', error);
    }
}

// 加载用户发布的商品
async function loadUserItems() {
    if (!currentUser) return;
    
    const userItems = document.getElementById('userItems');
    if (!userItems) return;
    
    try {
        const items = await apiRequest(`/items/seller/${currentUser.id}`);
        userItems.innerHTML = '';
        
        if (items.length === 0) {
            userItems.innerHTML = '<p class="empty-state">暂无发布的商品</p>';
            return;
        }
        
        items.forEach(item => {
            const itemElement = createItemCard(item);
            userItems.appendChild(itemElement);
        });
    } catch (error) {
        userItems.innerHTML = '<p class="empty-state">加载商品失败</p>';
        console.error('加载用户商品失败:', error);
    }
}

// 创建商品卡片
function createItemCard(item) {
    const itemDiv = document.createElement('div');
    itemDiv.className = 'item-card';
    
    itemDiv.innerHTML = `
        <div class="item-header">
            <h4 class="item-name">${item.name}</h4>
            <div class="item-price">¥${item.price.toFixed(2)}</div>
        </div>
        <p class="item-description">${item.description}</p>
        <div class="item-meta">
            <span>库存: ${item.stock}</span>
            <span>状态: ${item.available ? '在售' : '下架'}</span>
        </div>
        <div class="item-actions">
            <button onclick="editItem(${item.id})" class="btn btn-secondary btn-sm">编辑</button>
            <button onclick="deleteItem(${item.id})" class="btn btn-secondary btn-sm">删除</button>
        </div>
    `;
    
    return itemDiv;
}

// 编辑商品
function editItem(itemId) {
    // TODO: 实现商品编辑功能
    alert('商品编辑功能开发中...');
}

// 删除商品
async function deleteItem(itemId) {
    if (!confirm('确定要删除这个商品吗？')) {
        return;
    }
    
    try {
        // TODO: 实现商品删除API调用
        // const response = await apiRequest(`/items/${itemId}`, { method: 'DELETE' });
        // if (response.success) {
        //     alert('商品删除成功');
        //     loadUserItems();
        // } else {
        //     alert('商品删除失败');
        // }
        alert('商品删除功能开发中...');
    } catch (error) {
        alert('删除商品失败: ' + error.message);
        console.error('删除商品失败:', error);
    }
}


// 订单标签页功能
function showBuyerOrders() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelector('.tab-btn[onclick="showBuyerOrders()"]').classList.add('active');
    document.getElementById('buyerOrders').style.display = 'block';
    document.getElementById('sellerOrders').style.display = 'none';
    loadBuyerOrders();
}

function showSellerOrders() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelector('.tab-btn[onclick="showSellerOrders()"]').classList.add('active');
    document.getElementById('buyerOrders').style.display = 'none';
    document.getElementById('sellerOrders').style.display = 'block';
    loadSellerOrders();
}

// 加载买家订单
async function loadBuyerOrders() {
    if (!currentUser) return;
    
    const buyerOrders = document.getElementById('buyerOrders');
    if (!buyerOrders) return;
    
    try {
        const orders = await apiRequest(`/orders/buyer/${currentUser.id}`);
        buyerOrders.innerHTML = '';
        
        if (orders.length === 0) {
            buyerOrders.innerHTML = '<p style="text-align: center; color: #586069;">暂无购买订单</p>';
            return;
        }
        
        for (const order of orders) {
            const orderElement = await createOrderElement(order);
            buyerOrders.appendChild(orderElement);
        }
    } catch (error) {
        buyerOrders.innerHTML = '<p style="text-align: center; color: #ff6b6b;">加载订单失败</p>';
        console.error('加载买家订单失败:', error);
    }
}

// 加载卖家订单
async function loadSellerOrders() {
    if (!currentUser) return;
    
    const sellerOrders = document.getElementById('sellerOrders');
    if (!sellerOrders) return;
    
    try {
        const orders = await apiRequest(`/orders/seller/${currentUser.id}`);
        sellerOrders.innerHTML = '';
        
        if (orders.length === 0) {
            sellerOrders.innerHTML = '<p style="text-align: center; color: #586069;">暂无卖出订单</p>';
            return;
        }
        
        for (const order of orders) {
            const orderElement = await createOrderElement(order);
            sellerOrders.appendChild(orderElement);
        }
    } catch (error) {
        sellerOrders.innerHTML = '<p style="text-align: center; color: #ff6b6b;">加载订单失败</p>';
        console.error('加载卖家订单失败:', error);
    }
}

// 加载用户订单（兼容旧版本）
async function loadUserOrders() {
    loadBuyerOrders();
}

// 创建订单元素
async function createOrderElement(order) {
    const orderDiv = document.createElement('div');
    orderDiv.className = 'order-item';
    
    try {
        // 获取买家信息
        const buyer = await apiRequest(`/users/${order.buyerId}`);
        // 获取卖家信息
        const seller = await apiRequest(`/users/${order.sellerId}`);
        // 获取商品信息
        const item = await apiRequest(`/items/${order.itemId}`);
        
        orderDiv.innerHTML = `
            <div class="order-header">
                <div class="order-id">订单 #${order.id}</div>
                <div class="order-status status-${order.status.toLowerCase()}">${order.status}</div>
            </div>
            <div class="order-details">
                <p><strong>商品:</strong> ${item.name}</p>
                <p><strong>数量:</strong> ${order.quantity}</p>
                <p><strong>总价:</strong> ¥${order.totalPrice.toFixed(2)}</p>
                <div class="user-info-section">
                    <div class="user-info-row">
                        <strong>买家信息:</strong>
                        <div class="user-details">
                            <p>姓名: ${buyer.username}</p>
                            <p>邮箱: ${buyer.email}</p>
                            <p>电话: ${buyer.phone}</p>
                        </div>
                    </div>
                    <div class="user-info-row">
                        <strong>卖家信息:</strong>
                        <div class="user-details">
                            <p>姓名: ${seller.username}</p>
                            <p>邮箱: ${seller.email}</p>
                            <p>电话: ${seller.phone}</p>
                        </div>
                    </div>
                </div>
                <p><strong>创建时间:</strong> ${new Date(order.createTime).toLocaleString()}</p>
                <div class="order-actions">
                    ${createOrderActions(order)}
                </div>
            </div>
        `;
    } catch (error) {
        console.error('获取订单信息失败:', error);
        // 如果获取信息失败，显示基本订单信息
        orderDiv.innerHTML = `
            <div class="order-header">
                <div class="order-id">订单 #${order.id}</div>
                <div class="order-status status-${order.status.toLowerCase()}">${order.status}</div>
            </div>
            <div class="order-details">
                <p><strong>商品ID:</strong> ${order.itemId}</p>
                <p><strong>数量:</strong> ${order.quantity}</p>
                <p><strong>总价:</strong> ¥${order.totalPrice.toFixed(2)}</p>
                <p><strong>创建时间:</strong> ${new Date(order.createTime).toLocaleString()}</p>
                <div class="order-actions">
                    ${createOrderActions(order)}
                </div>
            </div>
        `;
    }
    
    return orderDiv;
}

// 创建订单操作按钮
function createOrderActions(order) {
    const isBuyer = currentUser && order.buyerId === currentUser.id;
    const isSeller = currentUser && order.sellerId === currentUser.id;
    
    let actions = '';
    
    if (isBuyer) {
        // 买家操作
        if (order.status === 'PENDING' || order.status === 'CONFIRMED') {
            actions += `<button onclick="cancelOrder(${order.id})" class="btn btn-secondary btn-sm">取消订单</button>`;
        }
        if (order.status === 'CONFIRMED') {
            actions += `<button onclick="completeOrder(${order.id})" class="btn btn-primary btn-sm">确认收货</button>`;
        }
    }
    
    if (isSeller) {
        // 卖家操作
        if (order.status === 'PENDING') {
            actions += `<button onclick="confirmOrder(${order.id})" class="btn btn-primary btn-sm">确认订单</button>`;
        }
    }
    
    return actions;
}

// 卖家确认订单
async function confirmOrder(orderId) {
    if (!confirm('确定要确认这个订单吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}/confirm`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('订单确认成功！');
            // 刷新订单列表
            loadUserOrders();
            await refreshUserInfo();
        } else {
            alert('订单确认失败: ' + data.message);
        }
    } catch (error) {
        alert('订单确认失败: ' + error.message);
        console.error('确认订单失败:', error);
    }
}

// 买家确认收货
async function completeOrder(orderId) {
    if (!confirm('确定要确认收货吗？订单完成后将无法取消。')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}/complete`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('订单完成成功！');
            // 刷新订单列表
            loadUserOrders();
            await refreshUserInfo();
        } else {
            alert('订单完成失败: ' + data.message);
        }
    } catch (error) {
        alert('订单完成失败: ' + error.message);
        console.error('完成订单失败:', error);
    }
}

// 买家取消订单
async function cancelOrder(orderId) {
    if (!confirm('确定要取消这个订单吗？取消后资金将退还到您的账户。')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}/cancel`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('订单取消成功！资金已退还到您的账户。');
            // 刷新订单列表和用户信息
            loadUserOrders();
            await refreshUserInfo();
        } else {
            alert('订单取消失败: ' + data.message);
        }
    } catch (error) {
        alert('订单取消失败: ' + error.message);
        console.error('取消订单失败:', error);
    }
}

// 获取订单状态颜色
function getStatusColor(status) {
    const colors = {
        'PENDING': '#0366d6',
        'CONFIRMED': '#28a745',
        'COMPLETED': '#6f42c1',
        'CANCELLED': '#d73a49'
    };
    return colors[status] || '#6a737d';
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
            
            // 加载用户商品和订单
            loadUserItems();
            loadUserOrders();
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
