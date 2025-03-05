package ru.hse.routemood.gptMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.routemood.gpt.GptMessage;

interface GptMessageRepository extends JpaRepository<GptMessage, Long> {

}