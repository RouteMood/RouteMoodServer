package ru.hse.routemood.gptMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.routemood.gpt.GptMessage;

public interface GptMessageRepository extends JpaRepository<GptMessage, Long> {

}