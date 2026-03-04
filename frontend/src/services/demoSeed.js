import { addMinutes, isoNow } from '../utils/time';

export function buildSeed() {
    const now = new Date();
    const startSoon = addMinutes(now, 30);
    const endSoon = addMinutes(now, 30 + 90);
    const startPast = addMinutes(now, -300);
    const endPast = addMinutes(now, -120);

    return {
        meta: { version: 1, seededAt: isoNow() },
        users: [
            {
                id: 'u_01',
                role: 'user',
                username: '230056PCNCT',
                fullName: 'Nguyễn Văn A',
                email: 'sv1@demo.local',
                password: '123456',
                studentCode: '230056PCNCT',
            },
            {
                id: 'u_02',
                role: 'user',
                username: '230728PCNCT',
                fullName: 'Trần Thị B',
                email: 'sv2@demo.local',
                password: '123456',
                studentCode: '230728PCNCT',
            },
            {
                id: 'u_03',
                role: 'user',
                username: '230070PCNCT',
                fullName: 'Lê Văn C',
                email: 'sv3@demo.local',
                password: '123456',
                studentCode: '230070PCNCT',
            },
        ],
        exams: [
            {
                id: 'e_practice_01',
                code: '230756PCNCT',
                title: 'Luyện tập quay lui',
                description: 'Bộ câu hỏi luyện tập – không giới hạn thời gian truy cập.',
                category: 'Luyện tập',
                mode: 'free',
                startAt: null,
                endAt: null,
                durationMinutes: 90,
                questions: [
                    {
                        id: 'q1',
                        text: 'Thuật toán quay lui (backtracking) thường dùng để giải nhóm bài toán nào?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Bài toán tối ưu tuyến tính' },
                            { id: 'B', label: 'B', text: 'Bài toán liệt kê/đệ quy có ràng buộc (tìm kiếm theo nhánh)' },
                            { id: 'C', label: 'C', text: 'Bài toán sắp xếp nhanh' },
                            { id: 'D', label: 'D', text: 'Bài toán tìm đường đi ngắn nhất (Dijkstra)' },
                        ],
                        correctChoiceId: 'B',
                        explanation: 'Backtracking phù hợp cho bài toán tổ hợp/đệ quy với ràng buộc (N-queens, subset, permutation…).',
                    },
                    {
                        id: 'q2',
                        text: 'Trong backtracking, điều kiện “cắt tỉa” (pruning) nhằm mục đích gì?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Tăng số nhánh phải duyệt' },
                            { id: 'B', label: 'B', text: 'Giảm không gian tìm kiếm bằng cách loại bỏ nhánh không khả thi' },
                            { id: 'C', label: 'C', text: 'Chuyển đệ quy thành vòng lặp' },
                            { id: 'D', label: 'D', text: 'Loại bỏ biến toàn cục' },
                        ],
                        correctChoiceId: 'B',
                        explanation: 'Pruning giúp bỏ qua những nhánh chắc chắn không dẫn đến nghiệm hợp lệ.',
                    },
                    {
                        id: 'q3',
                        text: 'Độ phức tạp của backtracking thường như thế nào?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Luôn O(n)' },
                            { id: 'B', label: 'B', text: 'Luôn O(n log n)' },
                            { id: 'C', label: 'C', text: 'Có thể rất lớn (thường là lũy thừa), phụ thuộc pruning và bài toán' },
                            { id: 'D', label: 'D', text: 'Luôn O(1)' },
                        ],
                        correctChoiceId: 'C',
                        explanation: 'Backtracking có thể duyệt nhiều nhánh; pruning tốt sẽ giúp giảm đáng kể.',
                    },
                ],
            },
            {
                id: 'e_mid_01',
                code: '230056PCNCT',
                title: 'Kiểm tra giữa kì',
                description: 'Bài kiểm tra theo khung giờ cụ thể.',
                category: 'Kiểm tra',
                mode: 'scheduled',
                startAt: startSoon.toISOString(),
                endAt: endSoon.toISOString(),
                durationMinutes: 90,
                questions: [
                    {
                        id: 'q1',
                        text: 'HTTP status code nào biểu thị “Not Found”?',
                        choices: [
                            { id: 'A', label: 'A', text: '200' },
                            { id: 'B', label: 'B', text: '301' },
                            { id: 'C', label: 'C', text: '404' },
                            { id: 'D', label: 'D', text: '500' },
                        ],
                        correctChoiceId: 'C',
                        explanation: '404 Not Found.',
                    },
                    {
                        id: 'q2',
                        text: 'Trong React, hook nào dùng để quản lý state cục bộ?',
                        choices: [
                            { id: 'A', label: 'A', text: 'useFetch' },
                            { id: 'B', label: 'B', text: 'useState' },
                            { id: 'C', label: 'C', text: 'useForm' },
                            { id: 'D', label: 'D', text: 'useRouter' },
                        ],
                        correctChoiceId: 'B',
                        explanation: 'useState quản lý state cục bộ của component.',
                    },
                ],
            },
            {
                id: 'e_final_01',
                code: '230728PCNCT',
                title: 'Kiểm tra cuối kì II năm 2025 - 2026',
                description: 'Đề thi demo theo khung giờ (đã hết hạn).',
                category: 'Cuối kỳ',
                mode: 'scheduled',
                startAt: startPast.toISOString(),
                endAt: endPast.toISOString(),
                durationMinutes: 30,
                questions: [
                    {
                        id: 'q1',
                        text: 'Chủ nghĩa Mác nói chung, triết học Mác nói riêng ra đời vào những năm nào của thế kỷ XIX?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Những năm 30 của thế kỷ XIX' },
                            { id: 'B', label: 'B', text: 'Những năm 40 của thế kỷ XIX' },
                            { id: 'C', label: 'C', text: 'Những năm 50 của thế kỷ XIX' },
                            { id: 'D', label: 'D', text: 'Những năm 20 của thế kỷ XIX' },
                        ],
                        correctChoiceId: 'B',
                        explanation: 'Triết học Mác ra đời khoảng những năm 40 thế kỷ XIX.',
                    },
                    {
                        id: 'q2',
                        text: 'Quan điểm cho rằng: mọi sự vật, hiện tượng chỉ là “phức hợp những cảm giác” của con người thể hiện lập trường triết học nào?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Chủ nghĩa duy tâm chủ quan' },
                            { id: 'B', label: 'B', text: 'Chủ nghĩa duy tâm khách quan' },
                            { id: 'C', label: 'C', text: 'Chủ nghĩa duy vật siêu hình' },
                            { id: 'D', label: 'D', text: 'Chủ nghĩa duy vật biện chứng' },
                        ],
                        correctChoiceId: 'A',
                        explanation: '“Phức hợp những cảm giác” gắn với chủ nghĩa duy tâm chủ quan.',
                    },
                    {
                        id: 'q3',
                        text: 'Tiêu chuẩn để xác định lập trường thế giới quan của các triết gia và học thuyết là vấn đề nào?',
                        choices: [
                            { id: 'A', label: 'A', text: 'Vấn đề giai cấp' },
                            { id: 'B', label: 'B', text: 'Vấn đề con người' },
                            { id: 'C', label: 'C', text: 'Vấn đề dân tộc' },
                            { id: 'D', label: 'D', text: 'Vấn đề cơ bản của triết học' },
                        ],
                        correctChoiceId: 'D',
                        explanation: 'Vấn đề cơ bản của triết học là tiêu chuẩn phân biệt duy vật/duy tâm.',
                    },
                ],
            },
        ],
        attempts: [],
    };
}
