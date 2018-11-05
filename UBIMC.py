
# coding: utf-8

# In[185]:


# import the necessary packages

import matplotlib
import math
from numpy import *;
import pandas as pd
from sklearn.decomposition import NMF
get_ipython().run_line_magic('matplotlib', 'inline')
# matplotlib.use('Agg')
import matplotlib.pyplot as plt
from sklearn.datasets.samples_generator import make_blobs
import numpy as np
# from parameters import *
import time


# In[186]:


X,M,F_in,Label_in,Label_out,F_out,C=1,1,1,1,1,1,1
Q_in,Q_out,V_in,V_out = 1,1,1,1
N,T,K = 1,1,1
L_te,L_sp = 1,1
l0,l1,l2,l3,l4=1,1,1,1,1
task1,task2 = 0.5,0.3


# In[187]:


def init(M_vle,l0_vle,l1_vle,l2_vle,l3_vle,l4_vle,pre_nmf=False):
    global X,M,F_in,F_out,C,Label_in,Label_out
    global Q_in,Q_out,V_in,V_out
    global N,T,K
    global L_te,L_sp
    global l0,l1,l2,l3,l4
    count = 0
    X = np.mat(pd.read_csv('POI_matrix.txt',index_col=[0],header=None))
    M=M_vle
    F_in = np.mat(pd.read_csv('F_in.txt', header=None))
    F_out = np.mat(pd.read_csv('F_out.txt', header=None))
    Label_in = np.mat(zeros((784,24)))
    Label_out = np.mat(zeros((784,24)))
    for i in range(784):
        for j in range(24):
            if F_in[i,j]>4:
               Label_in[i,j] = 1
            if F_out[i,j]>4:
               Label_out[i,j] = 1
    N,T = F_in.shape
    Q_in = np.mat(np.random.rand(N,M))
    V_in = np.mat(np.random.rand(M,T))
    Q_out = np.mat(np.random.rand(N,M))
    V_out = np.mat(np.random.rand(M,T))
    if pre_nmf:
        model = NMF(n_components=M, init='random', random_state=0)
        Q_in = np.mat(model.fit_transform(F_in))
        X_I = X.I
        U_in = X_I*Q_in
        V_in = np.mat(model.components_)

        model = NMF(n_components=M, init='random', random_state=0)
        Q_out = np.mat(model.fit_transform(F_out))
        U_out = X_I*Q_out
        V_out = np.mat(model.components_)
    
    C = np.mat(pd.read_csv('C.txt', header=None))
    
    K = C.shape[1]
    l0 = float(l0_vle)
    l1 = float(l1_vle)
    l2 = float(l2_vle)
    l3 = float(l3_vle)
    l4 = float(l4_vle)
    Z_te = np.mat(np.zeros((24,24)))
    D_te = np.mat(np.zeros((24,24)))
    for i in range(23):
        Z_te[i,i+1] = time_f(i)
        Z_te[i+1,i] = time_f(i)
    for i in range(24):
        for j in range(24):
            D_te[i,i] += Z_te[i,j]
    L_te = D_te - Z_te
    D_sp = np.mat(np.zeros((784,784)))
    Z_sp = np.mat(np.zeros((784,784)))
    for i in range(784):
        for j in range(784):
            D_sp[i,i] += dist_f(i,j)
            Z_sp[i,j] = dist_f(i,j)
    L_sp = D_sp-Z_sp
    
    
def dist_f(a,b):
    a_x = a/28
    a_y = a%28
    b_x = b/28
    b_y = b%28
    dist = abs(a_x - b_x) + abs(a_y - b_y)
    
    return np.exp(-dist + 1 )


def time_f(i):
    if (i%24 <18) and (i%24 >8):
        return 0
    return 1


def norm2(x):
    return np.linalg.norm(x)


# In[188]:


def loss_compute():
    loss = 0
    l0_loss = 0
    l0_loss += norm2(np.multiply(Label_in,F_in-Q_in*V_in))**2 
    #l0_loss += norm2(F_in-Q_in*V_in)**2 
    l0_loss += norm2(np.multiply(Label_out,F_out - Q_out*V_out))**2
    loss += l0_loss
    l1_loss = 0
    l1_loss += 2 * np.trace(Q_in.T*L_sp*Q_in)
    l1_loss += 2 * np.trace(Q_out.T*L_sp*Q_out)
    l1_loss *= 1.0 * l1/2
    loss += l1_loss
#     loss += 1.0*l1/2*l1_loss
    l2_loss = 0
    l2_loss += np.trace(V_in*L_te*V_in.T)
    l2_loss += np.trace(V_out*L_te*V_out.T)
    l2_loss *= 1.0 * l2/2
    loss += l2_loss
#     loss += 1.0*l2/2*l2_loss
    l3_loss = 0
    l3_loss += norm2(C.T*(Q_in*V_in - Q_out*V_out))**2
    l3_loss *= 1.0 * l3/2
    loss += l3_loss
#     loss += 1.0*l3/2*l3_loss
    l4_loss = sum(map(lambda x:norm2(x)**2,[Q_in,Q_out,V_in,V_out]))
    l4_loss *= 1.0*l4/2
    loss += l4_loss
#     loss += 1.0*l4/2*l4_loss
    return loss,l0_loss,l1_loss,l2_loss,l3_loss,l4_loss


def loss_compute_vector():
    loss = 0
    l0_loss = 0
    if l0 != 0:
        l0_loss += np.linalg.norm(np.multiply(Label_in,F_in - np.dot(Q_in, V_in))) ** 2
        l0_loss += np.linalg.norm(np.multiply(Label_out,F_out - np.dot(Q_out, V_out))) ** 2
    loss += l0 * l0_loss
    l1_loss = 0
    if l1 !=0:
        for i in range(N):
            for j in range(N):
                single_loss = 0
                single_loss += np.linalg.norm(Q_in[i,:] - Q_in[j,:]) ** 2
                single_loss += np.linalg.norm(Q_out[i,:] - Q_out[j,:]) ** 2
                l1_loss += 1.0 * dist_f(i,j) * (single_loss)
    l1_loss *= 1.0 * l1/2
    loss += l1_loss
#     loss += 1.0 * l1/2 * l1_loss
    l2_loss = 0
    if l2 != 0:
        for i in range(T-1):
            single_loss = 0
            single_loss += np.linalg.norm(V_in[:,i] - V_in[:,i+1]) ** 2
            single_loss += np.linalg.norm(V_out[:,i] - V_out[:,i+1]) ** 2
            l2_loss += 1.0 * time_f(i) * single_loss
    l2_loss *= 1.0 * l2/2
    loss += l2_loss
#     loss += 1.0 * l2/2 * l2_loss
    l3_loss = 0
    if l3 !=0:
        for k in range(K):
            single_loss = 0
            ma = np.dot(C[:,k].T, np.dot(Q_in, V_in) - np.dot(Q_out, V_out))
            single_loss += np.linalg.norm(ma) ** 2
            l3_loss += single_loss
    l3_loss *= 1.0 * l3/2
    loss += l3_loss
#     loss += 1.0 * l3/2 * l3_loss
    l4_loss = 0
    l4_loss += np.linalg.norm(Q_in) ** 2
    l4_loss += np.linalg.norm(Q_out) ** 2
    l4_loss += np.linalg.norm(V_in) ** 2
    l4_loss += np.linalg.norm(V_out) ** 2
    l4_loss *= 1.0*l4/2
    loss += l4_loss
#     loss += 1.0 * l4/2 * l4_loss
    return loss,l0_loss,l1_loss,l2_loss,l3_loss,l4_loss


def grad_Q_in():
    grad = Q_in - Q_in
    l0_grad = - 2*(F_in - Q_in*V_in)*V_in.T
    l1_grad = 2*l1*L_sp*Q_in
    l3_grad = l3*C*C.T*(Q_in*V_in-Q_out*V_out)*V_in.T
    l4_grad = l4*Q_in
    grad = l0_grad + l1_grad + l3_grad + l4_grad 
    return grad


def grad_Q_out():
    grad = Q_out - Q_out
    l0_grad = - 2*(F_out - Q_out*V_out)*V_out.T
    l1_grad = 2*l1*L_sp*Q_out
    l3_grad = - l3*C*C.T*(Q_in*V_in-Q_out*V_out)*V_out.T
    l4_grad = l4*Q_out
    grad = l0_grad + l1_grad + l3_grad + l4_grad 
    return grad


def grad_V_out():
    grad = V_out - V_out
    l0_grad = - 2*Q_out.T*(F_out - Q_out*V_out)
    l2_grad = 2*l2*V_out*L_te
    l3_grad = - l3*Q_out.T*C*C.T*(Q_in*V_in-Q_out*V_out)
    l4_grad = l4*V_out
    grad = l0_grad + l2_grad + l3_grad + l4_grad 
    return grad


def grad_V_in():
    grad = V_in - V_in
    l0_grad = - 2*Q_in.T*(F_in - Q_in*V_in)
    l2_grad = 2*l2*V_in*L_te
    l3_grad = l3*Q_in.T*C*C.T*(Q_in*V_in-Q_out*V_out)
    l4_grad = l4*V_in
    grad = l0_grad + l2_grad + l3_grad + l4_grad 
    return grad




# In[189]:


def GD(epoch,gamma,M_vle=20,l0_vle=1,l1_vle=1,l2_vle=1,l3_vle=1,l4_vle=1,verbose=False,pre_nmf=False):
    global Q_in,Q_out,V_in,V_out
    init(M_vle,l0_vle,l1_vle,l2_vle,l3_vle,l4_vle,pre_nmf)
    tot_line = []
    print 'GD begin!'
    for e in range(epoch):
        single_line = []
        loss_list = loss_compute()
        single_line.extend(loss_list)
#         if e > 20:
#             d_gamma = gamma * 10.0 /(1+e)
#         else:
#             d_gamma = gamma * 1.0 /(1+e)
        d_gamma = gamma
        g_Q_in = grad_Q_in()
        g_Q_out = grad_Q_out()
        g_V_in = grad_V_in()
        g_V_out = grad_V_out()
        single_line.extend(
            map(norm2,[g_Q_in,g_Q_out,g_V_in,g_V_out]))
        Q_in = Q_in - d_gamma * g_Q_in
        Q_out = Q_out - d_gamma * g_Q_out
        V_in = V_in - d_gamma * g_V_in
        V_Out = V_out - d_gamma * g_V_out
        tot_line.append(single_line)
               

        if verbose!=-1:
            if e %(10**verbose) == 0:
                print 'epoch:%d' %(e + 1)
                print '\tloss:%.5E\n'                   '\tl0_loss:%.5E'                   '\tl1_loss:%.5E'                   '\tl2_loss:%.5E\n'                   '\tl3_loss:%.5E'                   '\tl4_loss:%.5E' %(loss_list)

    tot_df = pd.DataFrame(tot_line)
    tot_df.columns = ['Total_loss', 
                      'L0_loss', 
                      'L1_loss', 
                      'L2_loss', 
                      'L3_loss', 
                      'L4_loss',
                      'grad_norm_Q_in',
                      'grad_norm_Q_out',
                      'grad_norm_V_in',
                      'grad_norm_V_out'
                     ]
    return tot_df


# # 执行这个跑结果

# The full edition of the optimization function is listed below：
#     
#     GD(epoch,gamma,M_vle=20,l0_vle=1,l1_vle=1,l2_vle=1,l3_vle=1,l4_vle=0.001,verbose=0,pre_nmf=False)
#     
#     verbose: 每隔多少10**verbose 个 epoch 输出结果， -1为不输出
#         如：设置为3，每隔1000个epoch进行输出

# In[190]:


get_ipython().run_line_magic('time', 'tot_df = GD(1000,0.00001,l1_vle=1,verbose=0,pre_nmf=False)')


# In[224]:


from __future__ import division
def acc(y,y_pre,label):
    count,count_co,acc = 0,0,0
    for i in range(784):
        for j in range(24):
            if label[i,j] == 1:
                count = count + 1
                #print y_pre[i,j], y[i,j]
                if y_pre[i,j] > y[i,j]:
                    count_co = count_co + 1
    acc = count_co/count
    return acc
                  


def er(y,y_pre,label):
    ER,Error,count = 0,0,0
    Error = np.mat(zeros((784,24)))
    Error = y - y_pre
    for i in range(784):
        for j in range(24):
            if label[i,j] == 1:
                ER += abs(Error[i,j])/y[i,j]
                count = count + 1
    ER = ER/count
    return ER
    
    
def rmsle(y, y_pre,label) : 
    err = 0
    count = 0
    for i in range(784):
        for j in range(24):
            if label[i,j] == 1:
                err += (np.log(1+y[i,j]) - np.log(1+y_pre[i,j]))**2
                count += 1
    rmsle = np.sqrt(err/count)
    return rmsle

print rmsle(F_in,Q_in*V_in,Label_in)
print rmsle(F_out,Q_out*V_out,Label_out)
print er(F_in,Q_in*V_in,Label_in)
print er(F_out,Q_out*V_out,Label_out)
print acc(F_in,Q_in*V_in,Label_in)
print acc(F_out,Q_out*V_out,Label_out)


# In[36]:


plt.figure(figsize=(8,8))
plt.plot(np.log10(tot_df.iloc[:,0]+1),label='Total')
plt.plot(np.log10(tot_df.iloc[:,1]+1),label='l0=%.3f'%l0)
plt.plot(np.log10(tot_df.iloc[:,2]+1),label='l1=%.3f'%l1)
plt.plot(np.log10(tot_df.iloc[:,3]+1),label='l2=%.3f'%l2)
plt.plot(np.log10(tot_df.iloc[:,4]+1),label='l3=%.3f'%l3)
plt.plot(np.log10(tot_df.iloc[:,5]+1),label='l4=%.3f'%l4)
plt.legend()
plt.title('Loss Plot')
plt.ylabel('Vle(Log10)')
plt.xlabel('Epoch Num')


# In[37]:


plt.figure(figsize=(8,8))
plt.plot(np.log10(tot_df.iloc[:,0]+1),label='Total')
plt.plot(np.log10(tot_df.iloc[:,1]+1),label='l0=%.3f'%l0)
plt.plot(np.log10(tot_df.iloc[:,2]+1),label='l1=%.3f'%l1)
plt.plot(np.log10(tot_df.iloc[:,3]+1),label='l2=%.3f'%l2)
plt.plot(np.log10(tot_df.iloc[:,4]+1),label='l3=%.3f'%l3)
plt.plot(np.log10(tot_df.iloc[:,5]+1),label='l4=%.3f'%l4)
plt.legend()
plt.title('Loss Plot')
plt.ylabel('Vle(Log10)')
plt.xlabel('Epoch Num')


# In[38]:


plt.figure(figsize=(8,8))
formatter = matplotlib.ticker.ScalarFormatter(useMathText=False)  #
formatter.set_scientific(False)     #使用科学计数法

# plt.plot(tot_df.iloc[:,0]+1,label='Total')
# plt.plot(tot_df.iloc[:,1]+1,label='l0=%.3f'%l0)
# plt.plot(tot_df.iloc[:,2]+1,label='l1=%.3f'%l1)
plt.plot(map(int,list(tot_df.iloc[:,3])),label='l2=%.3f'%l2)
# plt.plot(tot_df.iloc[:,4]+1,label='l3=%.3f'%l3)
# plt.plot(tot_df.iloc[:,5]+1,label='l4=%.3f'%l4)
plt.legend()
plt.title('Loss Plot')
plt.ylabel('Vle(Log10)')
plt.xlabel('Epoch Num')


# In[39]:


tot_df.tail()


# In[40]:


plt.plot(np.log10(tot_df.iloc[:,2]),label='l1')


# In[41]:


plt.figure(figsize=(16,8))
plt.plot(np.log10(tot_df.iloc[:,6]))
plt.plot(np.log10(tot_df.iloc[:,7]))
plt.plot(np.log10(tot_df.iloc[:,8]))
plt.plot(np.log10(tot_df.iloc[:,9]))
plt.legend([
    'grad_norm_Q_in',
    'grad_norm_Q_out',
    'grad_norm_V_in',
    'grad_norm_V_out'
])


# In[42]:


loss_compute()

