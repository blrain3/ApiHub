<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search } from "@element-plus/icons-vue";
import {
  createInterface,
  deleteInterface,
  fetchInterfaces,
  updateInterface,
  updateInterfaceStatus,
} from "@/api/admin";
import CanAccess from "@/components/CanAccess.vue";
import MethodBadge from "@/components/MethodBadge.vue";
import PageHeader from "@/components/PageHeader.vue";
import { useAccess } from "@/composables/useAccess";
import { withLoading } from "@/utils/async";
import type { ApiInterface } from "@/types";

const router = useRouter();
const { Perm, isAdmin } = useAccess();
const loading = ref(false);
const saving = ref(false);
const keyword = ref("");
const status = ref<number | "">("");
const list = ref<ApiInterface[]>([]);
const dialog = ref(false);
const editing = ref<ApiInterface | null>(null);
const form = reactive({
  name: "",
  method: "GET",
  path: "",
  version: "v1",
  category: "",
  description: "",
});

const filtered = computed(() =>
  list.value.filter((item) => {
    const hit =
      !keyword.value ||
      item.name.toLowerCase().includes(keyword.value.toLowerCase()) ||
      item.path.toLowerCase().includes(keyword.value.toLowerCase());
    return hit && (status.value === "" || item.status === status.value);
  })
);

async function loadList() {
  const result = await withLoading(loading, () => fetchInterfaces());
  if (result) list.value = result;
}

onMounted(() => {
  loadList();
});

function openCreate() {
  editing.value = null;
  Object.assign(form, { name: "", method: "GET", path: "", version: "v1", category: "", description: "" });
  dialog.value = true;
}

function openEdit(item: ApiInterface) {
  editing.value = item;
  Object.assign(form, {
    name: item.name,
    method: item.method,
    path: item.path,
    version: item.version,
    category: item.category || "",
    description: item.description || "",
  });
  dialog.value = true;
}

async function save() {
  if (!form.name.trim() || !form.path.trim()) {
    ElMessage.warning("请填写接口名称和路径");
    return;
  }
  const payload = {
    name: form.name.trim(),
    method: form.method,
    path: form.path.trim(),
    version: form.version || "v1",
    category: form.category.trim() || undefined,
    description: form.description.trim() || undefined,
  };
  try {
    saving.value = true;
    if (editing.value) {
      const updated = await updateInterface(editing.value.id, payload);
      const idx = list.value.findIndex((i) => i.id === updated.id);
      if (idx >= 0) list.value[idx] = updated;
      else list.value.unshift(updated);
      ElMessage.success("接口已更新");
    } else {
      const created = await createInterface(payload);
      list.value.unshift(created);
      ElMessage.success("接口已创建（默认下线，请手动上线）");
    }
    dialog.value = false;
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : "保存失败");
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(item: ApiInterface) {
  const next = item.status === 1 ? 0 : 1;
  try {
    await updateInterfaceStatus(item.id, next);
    item.status = next;
    ElMessage.success(next === 1 ? "已上线" : "已下线");
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : "状态更新失败");
  }
}

async function remove(item: ApiInterface) {
  try {
    await ElMessageBox.confirm(`确认删除接口「${item.name}」？相关应用开通关系也会一并清除。`, "删除确认", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
  } catch {
    return;
  }
  try {
    await deleteInterface(item.id);
    list.value = list.value.filter((i) => i.id !== item.id);
    ElMessage.success("已删除");
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : "删除失败");
  }
}

function goDetail(item: ApiInterface) {
  router.push(`/interfaces/${item.id}`);
}
</script>

<template>
  <div class="page" v-loading="loading">
    <PageHeader
      :title="isAdmin ? '接口资产中心' : '开放接口市场'"
      :desc="isAdmin ? '发布、编辑与上下线企业开放 API' : '浏览可调用的开放接口，查看文档后在应用中开通'"
    >
      <CanAccess :permission="Perm.API_MANAGE">
        <el-button type="primary" :icon="Plus" @click="openCreate">创建接口</el-button>
      </CanAccess>
    </PageHeader>

    <div class="card toolbar">
      <el-input v-model="keyword" placeholder="搜索接口名称或路径" clearable :prefix-icon="Search" style="width: 280px" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 140px">
        <el-option label="已上线" :value="1" />
        <el-option label="已下线" :value="0" />
      </el-select>
      <span class="count">共 {{ filtered.length }} 个资产</span>
    </div>

    <div class="market">
      <article v-for="item in filtered" :key="item.id" class="card api-card">
        <div class="card-top">
          <div>
            <h3>{{ item.name }}</h3>
            <p>{{ item.description || "暂无描述" }}</p>
          </div>
          <el-tag size="small" :type="item.status === 1 ? 'success' : 'info'">
            {{ item.status === 1 ? "已上线" : "已下线" }}
          </el-tag>
        </div>

        <div class="path-row">
          <MethodBadge :method="item.method" />
          <code>{{ item.path }}</code>
        </div>

        <div class="meta">
          <span>版本 {{ item.version }}</span>
          <span>调用 {{ (item.callCount || 0).toLocaleString() }}</span>
          <span>负责人 {{ item.owner || "Admin" }}</span>
        </div>

        <div class="actions">
          <el-button @click="goDetail(item)">查看详情</el-button>
          <CanAccess :permission="Perm.API_MANAGE">
            <el-button @click="openEdit(item)">编辑</el-button>
            <el-button :type="item.status === 1 ? 'warning' : 'success'" @click="toggleStatus(item)">
              {{ item.status === 1 ? "下线" : "上线" }}
            </el-button>
            <el-button type="danger" plain @click="remove(item)">删除</el-button>
          </CanAccess>
        </div>
      </article>
    </div>

    <el-empty v-if="!loading && filtered.length === 0" description="没有匹配的接口资产" />

    <el-dialog v-model="dialog" :title="editing ? '编辑接口' : '创建接口'" width="520px">
      <el-form label-position="top">
        <el-form-item label="接口名称" required>
          <el-input v-model="form.name" placeholder="例如 Weather API" />
        </el-form-item>
        <el-form-item label="请求方式" required>
          <el-select v-model="form.method" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" required>
          <el-input v-model="form.path" placeholder="/api/open/weather" />
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="form.version" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="生活服务 / AI / 通信" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  margin-bottom: 16px;
}
.count {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-muted);
}
.market {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
.api-card {
  padding: 20px 22px 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.card-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}
.card-top h3 {
  margin: 0 0 6px;
  font-size: 16px;
}
.card-top p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.5;
}
.path-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(15, 23, 42, 0.65);
  border: 1px solid var(--border);
}
code {
  font-family: ui-monospace, Consolas, monospace;
  font-size: 13px;
  color: var(--accent);
}
.meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--text-muted);
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 4px;
}
@media (max-width: 1100px) {
  .market {
    grid-template-columns: 1fr;
  }
}
</style>
