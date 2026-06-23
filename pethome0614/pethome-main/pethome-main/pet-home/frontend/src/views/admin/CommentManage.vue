<template>
  <div class="comment-manage">
    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索用户名/评论内容" @keyup.enter.native="handleSearch" class="search-input">
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
    </div>

    <el-table :data="comments" border class="comment-table" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column label="用户" width="120">
        <template slot-scope="scope">{{ scope.row.username || '-' }}</template>
      </el-table-column>
      <el-table-column prop="product_name" label="商品" width="160"></el-table-column>
      <el-table-column label="评分" width="80">
        <template slot-scope="scope">
          <span style="color: #ff9900; font-weight: 600;">{{ scope.row.rating }}星</span>
        </template>
      </el-table-column>
      <el-table-column label="评论内容" min-width="200">
        <template slot-scope="scope">
          <div class="content-cell">{{ scope.row.content }}</div>
        </template>
      </el-table-column>
      <el-table-column label="回复" min-width="180">
        <template slot-scope="scope">
          <div v-if="scope.row.reply" class="reply-cell">
            <el-tag type="success" size="mini">已回复</el-tag>
            <span class="reply-text">{{ scope.row.reply }}</span>
          </div>
          <el-tag v-else type="info" size="mini">未回复</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评论时间" width="170">
        <template slot-scope="scope">{{ formatTime(scope.row.create_time) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="openReplyDialog(scope.row)">
            {{ scope.row.reply ? '修改回复' : '回复' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > pageSize" class="pagination" background layout="prev, pager, next"
      :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchComments">
    </el-pagination>

    <!-- 回复弹窗 -->
    <el-dialog title="回复评论" :visible.sync="replyDialogVisible" width="520px" :close-on-click-modal="false">
      <div v-if="currentComment" class="reply-dialog-body">
        <div class="reply-info">
          <p><strong>用户：</strong>{{ currentComment.username }}</p>
          <p><strong>评分：</strong><el-rate :value="currentComment.rating" disabled></el-rate></p>
          <p><strong>评论内容：</strong></p>
          <div class="original-comment">{{ currentComment.content }}</div>
        </div>
        <el-input
          type="textarea"
          v-model="replyContent"
          :rows="4"
          placeholder="请输入回复内容..."
          maxlength="500"
          show-word-limit
        ></el-input>
      </div>
      <span slot="footer">
        <el-button @click="replyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submittingReply">提交回复</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminCommentList, replyComment } from '@/api/comment'

export default {
  name: 'CommentManage',
  data() {
    return {
      comments: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchKeyword: '',
      loading: false,
      // 回复
      replyDialogVisible: false,
      currentComment: null,
      replyContent: '',
      submittingReply: false
    }
  },
  mounted() {
    this.fetchComments()
  },
  methods: {
    async fetchComments() {
      this.loading = true
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize }
        if (this.searchKeyword.trim()) params.keyword = this.searchKeyword.trim()
        const res = await getAdminCommentList(params)
        this.comments = res.data.list || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchComments()
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    },
    openReplyDialog(comment) {
      this.currentComment = comment
      this.replyContent = comment.reply || ''
      this.replyDialogVisible = true
    },
    async submitReply() {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      this.submittingReply = true
      try {
        await replyComment({ commentId: this.currentComment.id, reply: this.replyContent.trim() })
        this.$message.success('回复成功')
        this.replyDialogVisible = false
        this.fetchComments()
      } catch (e) {
        console.error(e)
      }
      this.submittingReply = false
    }
  }
}
</script>

<style scoped>
.comment-manage {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input { width: 360px; }

.content-cell {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 13px;
  color: #555;
}

.reply-cell {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.reply-text {
  font-size: 13px;
  color: #555;
  max-height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

/* 回复弹窗 */
.reply-dialog-body { }

.reply-info {
  margin-bottom: 16px;
  font-size: 14px;
  color: #555;
}

.reply-info p {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.original-comment {
  background: #f8f9fb;
  border-radius: 8px;
  padding: 12px 16px;
  margin-top: 4px;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}
</style>
