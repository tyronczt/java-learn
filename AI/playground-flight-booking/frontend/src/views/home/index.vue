<template>
  <div class="__container_home_index">
    <div class="tech-background"></div>
    <a-row class="row" :gutter="[16, 16]">
      <a-col :span="8">
        <a-card class="card chat tech-card" :bordered="false">
          <template #title>
            <div class="tech-title">
              <Icon icon="mdi:robot" class="title-icon" />
              <label>AIFuture Airlines 智能助手</label>
            </div>
          </template>
          <div class="flex-grow">
            <a-card class="chat-body tech-chat-body" :bordered="false">
              <MessageList :list="messageInfo.list"></MessageList>
              <div
                id="chat-body-id"
                style="height: 5px; margin-top: 20px"
              ></div>
            </a-card>
          </div>
          <a-row class="footer" :gutter="16" style="align-items: center; margin-top: 8px; height: 40px">
            <a-col :span="20" style="display: flex; align-items: center">
              <a-input
                @keydown.enter="forHelp"
                v-model:value="question"
                placeholder="Type your message here..."
                class="tech-input"
                :bordered="false"
                style="margin-bottom: 0"
              >
                <template #prefix>
                  <Icon icon="mdi:message-text-outline" class="input-icon" />
                </template>
              </a-input>
            </a-col>
            <a-col :span="4" style="display: flex; align-items: center; justify-content: center; height: 100%">
              <a-button 
                @click="forHelp" 
                :disabled="lock" 
                type="primary"
                class="tech-button"
                style="height: 40px; margin-left: 8px; margin-top: 0"
              >
                <template #icon><Icon icon="mdi:send" /></template>
                Send
              </a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="16">
        <a-card class="card tech-card" :bordered="false">
          <template #title>
            <div class="tech-title">
              <Icon icon="mdi:airplane" class="title-icon" />
              <label>机票预定信息</label>
            </div>
          </template>
          <a-table
            :data-source="bookingInfo.dataSource"
            :columns="bookingInfo.columns"
            :pagination="false"
            class="tech-table"
            :rowClassName="() => 'tech-table-row'"
          >
            <template #bodyCell="{ record, index, column, text }">
              <template v-if="column.dataIndex === 'bookingStatus'">
                <template v-if="text === 'CONFIRMED'">
                  <div class="status-badge confirmed">
                    <Icon
                      class="status-icon"
                      icon="material-symbols:check-box-sharp"
                    />
                    <span>已确认</span>
                  </div>
                </template>
                <template v-else>
                  <div class="status-badge cancelled">
                    <Icon
                      class="status-icon"
                      icon="material-symbols:cancel-presentation-sharp"
                    />
                    <span>已取消</span>
                  </div>
                </template>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { PRIMARY_COLOR } from "@/base/constants";
import { nextTick, onMounted, reactive, ref } from "vue";
import { getBookings } from "@/api/service/booking";
import { Icon } from "@iconify/vue";
import Message from "@/views/home/Message.vue";
import MessageList from "@/views/home/MessageList.vue";
import type { MessageItem } from "@/types/message";
import { chat } from "@/api/service/assistant";
import { getUUID } from "ant-design-vue/lib/vc-dialog/util";
import { v4 as uuidv4 } from "uuid";
import { message } from "ant-design-vue";

const messageInfo: { cur: MessageItem | null; list: MessageItem[] } = reactive({
  cur: null,
  list: [
    {
      role: "assistant",
      content: "欢迎来到 AIFuture Airlines! 请问有什么可以帮您的?",
    },
  ],
});
const bookingInfo = reactive({
  dataSource: [],
  columns: [
    {
      title: "#",
      dataIndex: "bookingNumber",
      key: "bookingNumber",
    },
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "Date",
      dataIndex: "date",
      key: "date",
    },
    {
      title: "From",
      dataIndex: "from",
      key: "from",
    },
    {
      title: "To",
      dataIndex: "to",
      key: "to",
    },
    {
      title: "Status",
      dataIndex: "bookingStatus",
      key: "bookingStatus",
    },
    {
      title: "Booking Class",
      dataIndex: "bookingClass",
      key: "bookingClass",
    },
  ],
});
const question = ref("");
let scrollItem: any = null;

function scrollBottom() {
  scrollItem?.scrollIntoView({ behavior: "smooth", block: "end" });
}

function addMessage(role: "user" | "assistant", content: string) {
  let cur = {
    role,
    content,
  };
  messageInfo.cur = cur;
  messageInfo.list.push(cur);
  nextTick(() => {
    scrollBottom();
  });
}

const lock = ref(false);
function appendMessage(content: string) {
  if (messageInfo.cur) {
    messageInfo.cur.content += content;
  }
  scrollBottom();
}

const chatId = uuidv4();

function forHelp() {
  if (lock.value) {
    message.warn("助手正在生成, 请耐心等候");
    return;
  }
  let userMessage = question.value;
  if (!userMessage.trim()) {
    message.warning("请输入您的问题");
    return;
  }
  addMessage("user", userMessage);
  question.value = "";
  lock.value = true;
  const eventSource = new EventSource(
    `/api/assistant/chat?chatId=${chatId}&userMessage=${userMessage}`,
    {},
  );
  eventSource.onopen = function (event) {
    addMessage("assistant", "");
  };
  eventSource.onmessage = function (event) {
    appendMessage(event.data);
  };
  eventSource.onerror = function () {
    eventSource.close();
    bookings();
    lock.value = false;
  };
}

function bookings() {
  getBookings({}).then((res) => {
    bookingInfo.dataSource = res;
  });
}

let __null = PRIMARY_COLOR;
onMounted(() => {
  scrollItem = document.getElementById("chat-body-id");
  bookings();
});
</script>
<style lang="less" scoped>
.__container_home_index {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.tech-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.flex-grow {
  flex: 1;
  min-height: 0;
}

  .tech-background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.02) 25%, transparent 25%) -10px 0,
                linear-gradient(225deg, rgba(0, 0, 0, 0.02) 25%, transparent 25%) -10px 0,
                linear-gradient(315deg, rgba(0, 0, 0, 0.02) 25%, transparent 25%),
                linear-gradient(45deg, rgba(0, 0, 0, 0.02) 25%, transparent 25%);
    background-size: 20px 20px;
    background-color: #f0f2f5;
    z-index: 0;
  }

  .row {
    height: 100%;
    position: relative;
    z-index: 1;
    padding: 16px;
  }

  .tech-card {
    height: 100%;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);

    &:hover {
      box-shadow: 0 12px 36px rgba(0, 0, 0, 0.12);
      transform: translateY(-2px);
    }
  }

  .tech-title {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;

    .title-icon {
      margin-right: 8px;
      font-size: 24px;
      color: #1890ff;
    }
  }

  :deep(.ant-card-head) {
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    padding: 0 16px;
    background: linear-gradient(90deg, rgba(24, 144, 255, 0.05), transparent);
  }

  :deep(.ant-card-body) {
    height: calc(100vh - 180px);
    display: flex;
    flex-direction: column;
    padding: 16px;
    border-radius: 0;
  
    .tech-chat-body {
      border: none;
      height: calc(100% - 120px);
      overflow: auto;
      background: rgba(244, 245, 247, 0.7);
      border-radius: 8px;
      padding: 16px;
      box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.1);
        border-radius: 3px;
      }

      &::-webkit-scrollbar-track {
        background-color: transparent;
      }
    }
  }

  .flex-grow {
    flex-grow: 1;
  }

  .footer {
    width: 100%;
    margin-top: 16px;
  }

  .tech-input {
    border-radius: 24px;
    background: rgba(244, 245, 247, 0.7);
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
    padding: 8px 16px;
    transition: all 0.3s ease;

    &:hover, &:focus {
      background: rgba(244, 245, 247, 0.9);
      box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .input-icon {
      color: #1890ff;
      margin-right: 8px;
      vertical-align: middle;
    }
  }

  .tech-button {
    border-radius: 24px;
    height: 40px;
    min-width: 80px;
    width: auto;
    padding: 6px 16px;
    display: flex;
    align-items: center;
    line-height: 1;
  }

  .input-icon {
    color: #1890ff;
    margin-right: 4px;
    vertical-align: middle;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  }
  .tech-table {
    :deep(.ant-table) {
      background: transparent;
    }

    :deep(.ant-table-thead > tr > th) {
      background: rgba(24, 144, 255, 0.05);
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      font-weight: 600;
      color: #1a1a1a;
    }

    .tech-table-row {
      &:hover {
        background-color: #fafafa;
      }
    }
  }

  .status-badge {
    display: flex;
    align-items: center;
    padding: 4px 8px;
    border-radius: 16px;
    font-size: 12px;
    font-weight: 500;
    width: fit-content;

    &.confirmed {
      background-color: rgba(82, 196, 26, 0.1);
      color: #52c41a;
      border: 1px solid rgba(82, 196, 26, 0.2);
    }

    &.cancelled {
      background-color: rgba(190, 11, 74, 0.1);
      color: #be0b4a;
      border: 1px solid rgba(190, 11, 74, 0.2);
    }

    .status-icon {
      font-size: 16px;
      margin-right: 4px;
    }
  }

</style>
.tech-button {
  padding: 0;
  justify-content: center;
}

a-col[span="4"] {
  height: 48px;
  justify-content: center;
}

.tech-button :deep(svg) {
  vertical-align: middle;
  margin-bottom: 2px;
}
