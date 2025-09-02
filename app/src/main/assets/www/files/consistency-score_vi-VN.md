# Tính nhất quán rất quan trọng cho sự thành công trong giao dịch

_Con đường của một nhà giao dịch không phải là dễ dàng, thường là một hành trình gập ghềnh với nhiều thành công và thất bại trên đường đi. Đôi khi mọi thứ diễn ra suôn sẻ và một giao dịch thành công được tiếp nối bởi một giao dịch khác, trong khi những lúc khác chỉ là nỗi đau và sự khổ sở khi chứng kiến một loạt các giao dịch thua lỗ và tài khoản bị thổi bay._

Mỗi chiến lược đều có những giai đoạn hoạt động gần như hoàn hảo và tạo ra lợi nhuận, cũng như những giai đoạn nhà giao dịch phải đối mặt với một loạt thua lỗ. Tuy nhiên, nhìn chung, có thể lập luận rằng khi các giao dịch có lãi vượt trội hơn các giao dịch thua lỗ (về số lượng hoặc khối lượng), chiến lược đó sẽ có lãi trong dài hạn.

## Lợi nhuận nhanh không đảm bảo thành công

Do đó, khả năng sinh lời của một chiến lược không chỉ được xác định bởi quy mô lợi nhuận mà nó tạo ra, mà còn bởi hiệu suất của nó trong thời kỳ tốt và xấu. Nói cách khác, tính nhất quán của kết quả là một khía cạnh quan trọng cho bất kỳ chiến lược nào. Điều này có thể được định nghĩa theo nhiều cách khác nhau. Người ta có thể kiểm tra 100 giao dịch gần nhất, hoặc đánh giá kết quả cho mỗi quý và từ đó xác định xem chiến lược có sinh lời hay không trong dài hạn.

Có được tính nhất quán là rất quan trọng, như chúng ta có thể thấy trong các ví dụ về hai chiến lược sau đây.

**Chiến lược A:**

\- Một nhà giao dịch thực hiện 1 giao dịch mỗi ngày sử dụng cùng một kích thước vị thế trong 5 ngày liên tiếp.

\- Kích thước tài khoản: 100.000 USD

\- 5 giao dịch

\- Lợi nhuận 50.000 USD (50%)

\- Drawdown 50.000 USD (50%)

**Chiến lược B:**

\- Một nhà giao dịch thực hiện 1 giao dịch mỗi ngày sử dụng cùng một kích thước vị thế trong 100 ngày liên tiếp.

\- 100 giao dịch

\- Lợi nhuận 30.000 USD (30%)

\- Drawdown 10.000 USD (10%)

Mặc dù Chiến lược A tạo ra lợi nhuận lớn hơn nhiều cho mỗi giao dịch, Chiến lược B hoạt động tốt hơn trong thời gian dài hơn với mức drawdown thấp hơn, làm cho chiến lược ít rủi ro hơn và nhất quán hơn.

Mỗi nhà giao dịch nên nhận ra càng sớm càng tốt rằng việc theo đuổi lợi nhuận nhanh chóng làm tăng rủi ro của chiến lược và không phải là con đường dẫn đến giao dịch bền vững dài hạn. Trong Chiến lược A, có thể thấy rõ ràng rằng nó đã trải qua mức drawdown 50% chỉ trong một tuần, điều này có thể sớm hay muộn dẫn đến việc reset tài khoản.

## Điểm Nhất quán mới trong Account MetriX

Prop Trader đã ra mắt một chỉ số mới trong ứng dụng Account MetriX có tên là "Điểm Nhất quán" (Consistency Score), để cho phép các nhà giao dịch theo dõi tính nhất quán của họ. Chỉ số này đo lường tính nhất quán trong giao dịch của mỗi nhà giao dịch, với giá trị cao hơn cho thấy tính nhất quán cao hơn của các giao dịch, điều này có thể giúp các nhà giao dịch của chúng tôi đạt được kết quả tốt hơn, đây nên là mục tiêu cho bất kỳ ai muốn duy trì trong lĩnh vực giao dịch trong thời gian dài hơn.

Công thức tính toán Điểm Nhất quán rất đơn giản: (1 – (giá trị tuyệt đối của ngày có lãi hoặc lỗ nhiều nhất / kết quả tuyệt đối của tất cả các ngày giao dịch)) x 100%.

**Ví dụ:**

Kết quả của nhà giao dịch:

1. Ngày 1: +100
2. Ngày 2: -500
3. Ngày 3: +800

![img](/files/Consist-priklad-1.jpg)

Lợi nhuận hàng ngày lớn nhất là 800.

Tổng tuyệt đối của tất cả các ngày giao dịch là 100 + 500 + 800 = 1400.

Điểm Nhất quán = (1-(800/1400))x100 = 43%.

## Cách hoạt động của Điểm Nhất quán

Nếu Điểm Nhất quán của một nhà giao dịch là 0%, điều đó có nghĩa là tất cả lợi nhuận của anh ta được tạo ra trong một ngày duy nhất. Do đó, rất khó để đánh giá tính nhất quán và khả năng sinh lời của một chiến lược dựa trên kết quả chỉ từ một ngày giao dịch.

Nếu Điểm Nhất quán là 60%, điều đó có nghĩa là ngày giao dịch thành công nhất của nhà giao dịch chiếm 40% tổng lợi nhuận của anh ta.

**Ví dụ:**

Kết quả của nhà giao dịch:

1. Ngày 1: +250
2. Ngày 2: -350
3. Ngày 3: +400

![img](/files/Consist-priklad-2.jpg)

Lợi nhuận hàng ngày lớn nhất là 400.

Tổng tuyệt đối của tất cả các ngày giao dịch là 250 + 350 + 400 = 1000.

Điểm Nhất quán = (1-(400/1000))x100 = 60%.

Theo phân tích dài hạn về các nhà giao dịch của chúng tôi, chúng tôi đã đi đến phân phối kết quả sau đây. Giá trị trên 80% được coi là rất tốt vì giá trị này cho thấy chiến lược có khả năng thành công trong dài hạn. Prop Trader đánh giá cao các nhà giao dịch có thể duy trì cách tiếp cận nhất quán bên cạnh lợi nhuận. Một lợi ích lớn cho các nhà giao dịch nhất quán là kế hoạch Mở rộng (Scaling plan) của chúng tôi cũng như việc điều chỉnh tỷ lệ chia sẻ lợi nhuận lên 90% có lợi cho nhà giao dịch.

Tuy nhiên, các nhà giao dịch không phải lo lắng về thành công của họ hoặc liệu việc đáp ứng các yêu cầu để vượt qua Quá trình Đánh giá sẽ phụ thuộc vào Điểm Nhất quán của họ hay không. Việc hoàn thành thành công Quá trình Đánh giá dựa trên việc hoàn thành các Mục tiêu Giao dịch cơ bản của Prop Trader và Điểm Nhất quán chỉ nhằm mục đích cung cấp thông tin.

Một số nhà giao dịch có thể cảm thấy rằng Điểm Nhất quán có thể giới hạn hoặc phân biệt đối xử với một số chiến lược nhất định, nhưng chắc chắn đây không phải là mục đích của nó. Phấn đấu để có kết quả nhất quán không giới hạn bất kỳ nhà giao dịch nào đạt được lợi nhuận trên mức trung bình. Đúng vậy, đôi khi một nhà giao dịch có thể có một giao dịch thành công đặc biệt trong đó anh ta nhận ra lợi nhuận trên mức trung bình, điều này có thể làm hại đến Điểm Nhất quán của anh ta. Tuy nhiên, giao dịch không phải là về một lần lãi, và việc duy trì kết quả nhất quán trong thời gian dài có thể không có tác động tiêu cực đến kết quả tổng thể.

Khi một nhà giao dịch giao dịch thành công trong thời gian dài với RRR là 2:1, ví dụ, anh ta có thể có một giao dịch đặc biệt khi anh ta giữ một giao dịch có lãi lâu hơn và đạt đến RRR là 4:1. Vì chiến lược của anh ta được thiết lập ở RRR 2:1 (chúng tôi giả định rằng anh ta đã kiểm tra nó trong thời gian dài và biết mình đang làm gì), đây có thể là một sự may mắn ngẫu nhiên không thể lặp lại và các nỗ lực tiếp theo cho các giao dịch siêu như vậy có thể kết thúc bằng một loạt thua lỗ không cần thiết. Không hiếm khi các can thiệp không cần thiết vào một chiến lược đang hoạt động tốt trong việc theo đuổi lợi nhuận nhanh hơn dẫn đến thua lỗ và, trong trường hợp xấu nhất, xóa sổ tài khoản.

Mặt khác, một cách tiếp cận nhất quán có thể cứu tài khoản của nhà giao dịch nếu anh ta trải qua một thời gian dài thua lỗ. Khi một nhà giao dịch quyết định "trả thù thị trường" và bắt đầu mở các vị thế lớn không cần thiết để theo đuổi lợi nhuận cao hơn, anh ta có thể đối mặt với việc xóa tài khoản, trong trường hợp của Prop Trader Challenge hoặc Prop Trader Account.

Điều này được minh họa tốt nhất bằng một ví dụ. Dưới đây chúng ta có thể thấy kết quả của một Prop Trader Account với Điểm Nhất quán là 80,61%, và sau một thời gian khi nhà giao dịch không hoạt động tốt, anh ta gần như đạt đến giới hạn Thua lỗ Tối đa. Nếu anh ta đã giao dịch các vị thế lớn hơn, hoặc bắt đầu mở các vị thế lớn hơn này sau một loạt thua lỗ, anh ta chắc chắn đã vi phạm điều kiện quan trọng này.

![img](/files/Consist-graf.png)

Nhưng nhờ cách tiếp cận nhất quán của mình, anh ta đã quản lý để thoát khỏi drawdown và cuối cùng tạo ra lợi nhuận lớn là 17.000 USD. Cũng rõ ràng từ ví dụ trên rằng một nhà giao dịch không nhất thiết phải có đường cong lợi suất hoàn toàn mượt mà với biến động tối thiểu mọi lúc để đạt được Điểm Nhất quán cao.

## Kết luận

Prop Trader đang tìm kiếm những nhà giao dịch nghiêm túc có khả năng quản lý rủi ro một cách có trách nhiệm và đạt được kết quả nhất quán trong thời gian dài. Từ nhiều năm kinh nghiệm của chúng tôi, chúng tôi biết rằng một cách tiếp cận nhất quán phân biệt các nhà giao dịch tuân theo kế hoạch giao dịch với những nhà giao dịch chỉ kiếm lợi nhuận thông qua may mắn. Chúng tôi hy vọng tính năng mới này sẽ giúp tất cả các nhà giao dịch đạt được mục tiêu dài hạn của họ.

---

### Câu hỏi và câu trả lời thường gặp

- **Prop Trader đang cố gắng đạt được gì với Điểm Nhất quán?** Tại Prop Trader, chúng tôi đánh giá cao các nhà giao dịch coi trọng việc giao dịch. Chúng tôi không chỉ thích thấy lợi nhuận, mà còn thích thấy chúng được tạo ra một cách nhất quán trong một khoảng thời gian và dựa trên số lượng giao dịch đáng kể. Chỉ số Nhất quán chỉ mang tính thông tin và không ảnh hưởng đến kết quả của tài khoản giao dịch.

- **Điểm Nhất quán được tính toán như thế nào?** Chỉ số Nhất quán được tính toán bằng công thức sau: (1 - (Ngày Lãi hoặc Lỗ Cao nhất / Tổng Tuyệt đối của tất cả các Ngày Giao dịch)) x 100%.

- **Điểm Nhất quán có ảnh hưởng đến kết quả của Prop Trader Challenge / Prop Trader Account của tôi không?** Không, nó không ảnh hưởng. Bạn chỉ cần tuân theo các Mục tiêu Giao dịch và tránh các thực hành giao dịch bị cấm.

- **Bạn có kế hoạch thực hiện quy tắc dựa trên Điểm Nhất quán trong tương lai không?** Hiện tại, Điểm Nhất quán chỉ mang tính thông tin.
